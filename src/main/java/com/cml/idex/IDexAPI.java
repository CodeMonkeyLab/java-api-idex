package com.cml.idex;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.Sign.SignatureData;

import com.cml.idex.packets.CancelOrder;
import com.cml.idex.packets.Parser;
import com.cml.idex.packets.PlaceOrder;
import com.cml.idex.packets.Req;
import com.cml.idex.packets.Return24Volume;
import com.cml.idex.packets.ReturnBalances;
import com.cml.idex.packets.ReturnCompleteBalances;
import com.cml.idex.packets.ReturnContractAddress;
import com.cml.idex.packets.ReturnCurrencies;
import com.cml.idex.packets.ReturnDepositsWithdrawals;
import com.cml.idex.packets.ReturnNextNonce;
import com.cml.idex.packets.ReturnOpenOrders;
import com.cml.idex.packets.ReturnTicker;
import com.cml.idex.util.CancelSigParms;
import com.cml.idex.util.IdexCrypto;
import com.cml.idex.util.OrderSigParms;
import com.cml.idex.value.BalanceOrder;
import com.cml.idex.value.Currency;
import com.cml.idex.value.DepositsWithdrawals;
import com.cml.idex.value.Order;
import com.cml.idex.value.Outcome;
import com.cml.idex.value.Ticker;
import com.cml.idex.value.Volume;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IDexAPI {

   private String              version         = "v1";
   private static final String HTTP_ENDPOINT   = "https://api.idex.market/";
   private static final String WS_ENDPOINT     = "wss://v1.idex.market";
   private static final String CONTENT_TYPE    = "application/json";

   private final HttpClient    client          = HttpClient.newHttpClient();
   private final ObjectMapper  mapper          = new ObjectMapper();

   public final String         DEFAULT_ETH_ADR = "0x0000000000000000000000000000000000000000";

   public static void main(String[] args) {

      final String address = "0xBa0af722A67B15eB6d66ac3341A94B4f3A863107";

      IDexAPI iDexAPI = new IDexAPI();
      // System.out.println(iDexAPI.returnTicker("ETH_AURA").join().toString());
      //
      // iDexAPI.returnCurrencies().join().entrySet()
      // .forEach(entry -> System.out.println(entry.getKey() + " : " +
      // entry.getValue()));

      // Volume volume = iDexAPI.return24Volume().join();
      // volume.getVolumePairs().entrySet().stream()
      // .forEach(entry -> System.out.println(entry.getKey() + " : " +
      // entry.getValue()));
      // volume.getTotalVolumes().entrySet().stream()
      // .forEach(entry -> System.out.println(entry.getKey() + " : " +
      // entry.getValue()));

      // System.out.println(iDexAPI.returnBalances("0xBa0af722A67B15eB6d66ac3341A94B4f3A863107").join());
      //
      // iDexAPI.returnCompleteBalances("0xBa0af722A67B15eB6d66ac3341A94B4f3A863107").join().entrySet().stream()
      // .forEach(entry -> System.out.println(entry.getKey() + " : " +
      // entry.getValue()));

      // DepositsWithdrawals depWith =
      // iDexAPI.returnDepositsWithdrawals(address, null, null).join();
      //
      // System.out.println("Deposists");
      // depWith.getDeposits().forEach(System.out::println);
      // depWith.getWithdrawals().forEach(System.out::println);

      List<Order> orders = iDexAPI.returnOpenOrders(null, address, null, null).join();
      orders.forEach(System.out::println);
   }

   /**
    * 
    * @param tokenBuy
    *           The address of the token you will receive as a result of the
    *           trade.
    * @param amountBuy
    *           The amount of the token you will receive when the order is fully
    *           filled.
    * @param tokenSell
    *           The address of the token you will lose as a result of the trade
    * @param amountSell
    *           The amount of the token you will give up when the order is fully
    *           filled
    * @param address
    *           The address you are posting the order from
    * @param nonce
    *           One time number associated with the limit order
    * @param expires
    *           DEPRECATED this property has no effect on your limit order but
    *           is still REQUIRED to submit a limit order as it is one of the
    *           parameters that is hashed
    * @param v
    *           Derived from signing the hash of the message
    * @param r
    *           Derived from signing the hash of the message
    * @param s
    *           Derived from signing the hash of the message
    * @return
    */
   public CompletableFuture<Order> order(
         final String tokenBuy, final BigInteger amountBuy, final String tokenSell, final BigInteger amountSell,
         final String address, final long nonce, final long expires, final byte v, final byte[] r, final byte[] s) {

         // Contact Address : returnContractAddress()
         // nonce : returnNextNonce()
         return process(
               PlaceOrder.create(tokenBuy, amountBuy, tokenSell, amountSell, address, nonce, expires, v, r, s));
   }

   public CompletableFuture<Order> order(
         Credentials credentials, final String tokenBuy, final BigInteger amountBuy, final String tokenSell,
         final BigInteger amountSell, final String address, final Long expires) {

         final CompletableFuture<Long> nounceF = returnNextNonce(address);
         final CompletableFuture<String> ctcAdrF = returnContractAddress();

      return CompletableFuture.allOf(ctcAdrF, nounceF).thenApply(v -> {
         try {
            return IdexCrypto.createParamsSig(new OrderSigParms(ctcAdrF.get(), tokenBuy, amountBuy, tokenSell,
                  amountSell, expires, nounceF.get(), address), credentials);
         } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
         }
      }).thenCompose(sigData -> {
         try {
            return this.order(tokenBuy, amountBuy, tokenSell, amountSell, address, nounceF.get(), expires,
                  sigData.getV(), sigData.getR(), sigData.getS());
         } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
         }
      });
   }
   
   /**
    * Cancels an order associated with the address.
    * 
    * @param orderHash
    *           The raw hash of the order you are cancelling.
    * @param address
    *           The address you are sending the cancel from must own the order.
    * @param nonce
    *           One time number associated with the address.
    * @param v
    *           Derived from signing the hash of the message.
    * @param r
    *           Derived from signing the hash of the message.
    * @param s
    *           Derived from signing the hash of the message.
    * @return Future
    */
   public CompletableFuture<Outcome> cancel(String orderHash, String address, long nonce, byte v, byte[] r, byte[] s) {
      return process(CancelOrder.create(orderHash, address, nonce, v, r, s));
   }

   public CompletableFuture<Outcome> cancel(final Credentials credentials, final String orderHash, String address) {
      return returnNextNonce(address).thenCompose(nonce -> {
         SignatureData sigData;
         try {
            sigData = IdexCrypto.createParamsSig(new CancelSigParms(orderHash, nonce), credentials);
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
         return this.cancel(orderHash, address, nonce, sigData.getV(), sigData.getR(), sigData.getS());
      });
   }

   /**
    * Returns the lowest nonce that you can use from the given address in one of
    * the contract-backed trade functions.
    * 
    * @return Future
    */
   public CompletableFuture<Long> returnNextNonce(final String address) {
      return process(ReturnNextNonce.create(address));
   }

   /**
    * Returns the lowest nonce that you can use from the given address in one of
    * the contract-backed trade functions.
    * 
    * @return Future
    */
   public CompletableFuture<String> returnContractAddress() {
      return process(ReturnContractAddress.create());
   }

   /**
    * Returns a paginated list of all open orders for a given market or address.
    * 
    * The response is similar to the response of returnOrderBook except that
    * orders are sorted chronologically (oldest first) instead of by price. The
    * data format of an order is the same, and there are some additional data
    * points.
    * 
    * @param market
    *           Required if address not specified.
    * @param address
    *           Required if market not specified. Returns all open orders placed
    *           by the given address.
    * @param count
    *           Number of records to be returned per request. Default 10
    * @param cursor
    *           For pagination. Provide the value returned in the
    *           idex-next-cursor HTTP header to request the next slice (or
    *           page). This endpoint uses the orderNumber property of a record
    *           for the cursor.
    * @return Future
    */
   public CompletableFuture<List<Order>> returnOpenOrders(
         final String market, final String address, final Integer count, final Long cursor
      ) {
      return process(ReturnOpenOrders.create(market, address, count, cursor));
      }

      /**
       * Returns your deposit and withdrawal history within a range, specified
       * by the "start" and "end" optional properties. Withdrawals can be marked
       * as "PENDING" if they are queued for dispatch, "PROCESSING" if the
       * transaction has been dispatched, and "COMPLETE" if the transaction has
       * been mined.
       * 
       * @param address
       *           Address of the wallet
       * @param start
       *           Optional Inclusive starting UNIX timestamp of returned result
       * @param end
       *           Optional Inclusive ending UNIX timestamp of returned results.
       *           Defaults to current timestamp
       * @return
       */
   public CompletableFuture<DepositsWithdrawals> returnDepositsWithdrawals(
         final String address, final LocalDateTime start, final LocalDateTime end
      ) {
      return process(ReturnDepositsWithdrawals.create(address, start, end));
      }

      /**
       * Returns available balances for an address along with the amount of open
       * orders for each token, indexed by token symbol.
       * 
       * @param address
       *           ETH Address
       * @return Future
       */
      public CompletableFuture<Map<String, BalanceOrder>> returnCompleteBalances(final String address) {
      return process(ReturnCompleteBalances.create(address));
   }

   /**
    * Returns available balances for an address(total deposited minus amount in
    * open orders) indexed by token symbol.
    * 
    * @param address
    *           ETH Address
    * @return Future
    */
   public CompletableFuture<Map<String, BigDecimal>> returnBalances(final String address) {
      return process(ReturnBalances.create(address));
   }

   /**
    * Returns the 24-hour volume for all markets, plus totals for primary
    * currencies.
    * 
    * @return Future
    */
   public CompletableFuture<Volume> return24Volume() {
      return process(Return24Volume.create());
   }

   /**
    * Returns an object of token data indexed by symbol.
    * 
    * @return Future
    */
   public CompletableFuture<Map<String, Currency>> returnCurrencies() {
      return process(ReturnCurrencies.create());
   }

   /**
    * Designed to behave similar to the API call of the same name provided by
    * the Poloniex HTTP API, with the addition of highs and lows. Returns all
    * necessary 24 hr data.
    * 
    * Please note: If any field is unavailable due to a lack of trade history or
    * a lack of 24hr data, the field will be set to 'null'. percentChange,
    * baseVolume, and quoteVolume will never be 'null' but may be 0.
    * 
    * @param market
    *           Market eg. ETH_SAN, ETH_AURA
    * @return Future
    */
   public CompletableFuture<Ticker> returnTicker(final String market) {
      return sendAsync(ReturnTicker.create(market)).thenApply(httpRsp -> httpRsp.body())
            .thenApply(body -> ReturnTicker.fromJson(mapper, body, market));
   }

   private <V, T extends Parser<V> & Req> CompletableFuture<V> process(final T requestParser) {
      return sendAsync(requestParser).thenApply(httpRsp -> httpRsp.body())
            .thenApply(body -> requestParser.parse(mapper, body));
   }

   private CompletableFuture<HttpResponse<String>> sendAsync(Req req) {
      final HttpRequest httpreq = HttpRequest.newBuilder(URI.create(HTTP_ENDPOINT + req.getEndpoint()))
            .setHeader("Content-Type", CONTENT_TYPE).POST(BodyPublishers.ofString(req.getPayload())).build();
      return client.sendAsync(httpreq, HttpResponse.BodyHandlers.ofString());
   }
}
