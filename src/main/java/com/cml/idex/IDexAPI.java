package com.cml.idex;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.asynchttpclient.util.HttpConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.cml.idex.packets.ReturnCurrenciesWithPairs;
import com.cml.idex.packets.ReturnDepositsWithdrawals;
import com.cml.idex.packets.ReturnNextNonce;
import com.cml.idex.packets.ReturnOpenOrders;
import com.cml.idex.packets.ReturnOrderBook;
import com.cml.idex.packets.ReturnOrderStatus;
import com.cml.idex.packets.ReturnOrderTrades;
import com.cml.idex.packets.ReturnTicker;
import com.cml.idex.packets.ReturnTradeHistory;
import com.cml.idex.packets.SortOrder;
import com.cml.idex.packets.Withdraw;
import com.cml.idex.sig.CancelSigParms;
import com.cml.idex.sig.OrderSigParms;
import com.cml.idex.sig.WithdrawSigParms;
import com.cml.idex.util.IdexCrypto;
import com.cml.idex.util.Utils;
import com.cml.idex.value.BalanceOrder;
import com.cml.idex.value.Currency;
import com.cml.idex.value.CurrencyPairs;
import com.cml.idex.value.DepositsWithdrawals;
import com.cml.idex.value.Order;
import com.cml.idex.value.OrderBook;
import com.cml.idex.value.OrderTrade;
import com.cml.idex.value.Outcome;
import com.cml.idex.value.Ticker;
import com.cml.idex.value.TradeHistory;
import com.cml.idex.value.Volume;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IDexAPI implements Closeable {

   private static final Logger   log             = LoggerFactory.getLogger(IDexAPI.class);

   private static final String   HTTP_ENDPOINT   = "https://api.idex.market/";
   // private static final String WS_ENDPOINT = "wss://v1.idex.market";
   private static final String   CONTENT_TYPE    = "application/json";

   private final AsyncHttpClient client          = Dsl.asyncHttpClient();
   private final ObjectMapper    mapper          = new ObjectMapper();

   public static final String    DEFAULT_ETH_ADR = "0x0000000000000000000000000000000000000000";

   /**
    * Places a limit order on IDEX.
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
    * @return CompletableFuture of order action
    */
   public CompletableFuture<Order> order(
         final String tokenBuy, final BigInteger amountBuy, final String tokenSell, final BigInteger amountSell,
         final String address, final long nonce, final long expires, final byte[] v, final byte[] r, final byte[] s
   ) {
      return process(PlaceOrder.create(tokenBuy, amountBuy, tokenSell, amountSell, address, nonce, expires, v, r, s));
   }

   /**
    * Places a limit order on IDEX.
    *
    * @param credentials
    *           Wallet credentials
    * @param contractAdress
    *           The contract address used for depositing, withdrawing, and
    *           posting orders. Can get from returnContractAddress
    * @param nonce
    *           One time numeric value associated with your address. Can get
    *           from returnNextNonce.
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
    * @param expires
    *           DEPRECATED this property has no effect on your limit order but
    *           is still REQUIRED to submit a limit order as it is one of the
    *           parameters that is hashed
    * @return CompletableFuture of order action
    */
   public CompletableFuture<Order> order(
         Credentials credentials, final String contractAdress, final long nonce, final String tokenBuy,
         final BigInteger amountBuy, final String tokenSell, final BigInteger amountSell, final Long expires
   ) {
      final SignatureData sigData;
      try {
         sigData = IdexCrypto.createParamsSig(new OrderSigParms(contractAdress, tokenBuy, amountBuy, tokenSell,
               amountSell, expires, nonce, credentials.getAddress()), credentials);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
      return order(tokenBuy, amountBuy, tokenSell, amountSell, credentials.getAddress(), nonce, expires, sigData.getV(),
            sigData.getR(), sigData.getS());
   }

   /**
    * Places a limit order on IDEX.
    *
    * @param credentials
    *           Wallet credentials
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
    * @param expires
    *           DEPRECATED this property has no effect on your limit order but
    *           is still REQUIRED to submit a limit order as it is one of the
    *           parameters that is hashed
    * @return CompletableFuture of order action
    */
   public CompletableFuture<Order> order(
         Credentials credentials, final String tokenBuy, final BigInteger amountBuy, final String tokenSell,
         final BigInteger amountSell, final Long expires
   ) {

      final CompletableFuture<Long> nounceF = returnNextNonce(credentials.getAddress());
      final CompletableFuture<String> ctcAdrF = returnContractAddress();

      return CompletableFuture.allOf(ctcAdrF, nounceF).thenCompose(v -> {
         try {
            return order(credentials, ctcAdrF.get(), nounceF.get(), tokenBuy, amountBuy, tokenSell, amountSell,
                  expires);
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
    * @return CompletableFuture of cancel action
    */
   public CompletableFuture<Outcome> cancel(
         String orderHash, String address, long nonce, byte[] v, byte[] r, byte[] s
   ) {
      return process(CancelOrder.create(orderHash, address, nonce, v, r, s));
   }

   /**
    * Cancels an order associated with the address.
    *
    * @param credentials
    *           Wallet credentials
    * @param orderHash
    *           The raw hash of the order you are cancelling.
    * @return CompletableFuture of cancel action
    */
   public CompletableFuture<Outcome> cancel(final Credentials credentials, final String orderHash) {
      if (log.isDebugEnabled())
         log.debug("cancel: orderHash : " + orderHash);
      return returnNextNonce(credentials.getAddress()).thenCompose(nonce -> {
         SignatureData sigData;
         try {
            sigData = IdexCrypto.createParamsSig(new CancelSigParms(orderHash, nonce), credentials);
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
         return this.cancel(orderHash, credentials.getAddress(), nonce, sigData.getV(), sigData.getR(), sigData.getS());
      });
   }

   /**
    * Withdraws funds associated with the address. You cannot withdraw funds
    * that are tied up in open orders.
    *
    * @param address
    *           The address you are transacting from.
    * @param amount
    *           The raw amount you are withdrawing, not adjusted for token
    *           precision.
    * @param token
    *           The address of the token you are withdrawing from, Constant
    *           (IDexAPI.DEFAULT_ETH_ADR) for ETH.
    * @param nonce
    *           One time numeric value associated with your address. Can get
    *           from returnNextNonce.
    * @param v
    *           Value obtained from signing message hash.
    * @param r
    *           Value obtained from signing message hash.
    * @param s
    *           Value obtained from signing message hash.
    * @return CompletableFuture of withdraw action
    */
   public CompletableFuture<Outcome> withdraw(
         final String address, final BigInteger amount, final String token, long nonce, byte[] v, byte[] r, byte[] s
   ) {
      return process(Withdraw.create(address, amount, token, nonce, v, r, s));
   }

   /**
    * Withdraws funds associated with the address. You cannot withdraw funds
    * that are tied up in open orders.
    *
    * @param credentials
    *           Wallet credentials
    * @param contractAddress
    *           The contract address used for depositing, withdrawing, and
    *           posting orders. Can get from returnContractAddress
    * @param amount
    *           The raw amount you are withdrawing, not adjusted for token
    *           precision.
    * @param token
    *           The address of the token you are withdrawing from, Constant
    *           (IDexAPI.DEFAULT_ETH_ADR) for ETH.
    * @param nonce
    *           One time numeric value associated with your address. Can get
    *           from returnNextNonce.
    * @return CompletableFuture of withdraw action
    */
   public CompletableFuture<Outcome> withdraw(
         final Credentials credentials, final String contractAddress, final BigInteger amount, final String token,
         long nonce
   ) {
      final SignatureData sigData;
      try {
         sigData = IdexCrypto.createParamsSig(
               new WithdrawSigParms(contractAddress, token, amount, credentials.getAddress(), nonce), credentials);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
      return withdraw(credentials.getAddress(), amount, token, nonce, sigData.getV(), sigData.getR(), sigData.getS());
   }

   /**
    * Withdraws funds associated with the address. You cannot withdraw funds
    * that are tied up in open orders.
    *
    * @param credentials
    *           Wallet credentials
    * @param amount
    *           The raw amount you are withdrawing, not adjusted for token
    *           precision.
    * @param token
    *           The address of the token you are withdrawing from, Constant
    *           (IDexAPI.DEFAULT_ETH_ADR) for ETH.
    * @return Future
    */
   public CompletableFuture<Outcome> withdraw(
         final Credentials credentials, final BigInteger amount, final String token
   ) {
      final CompletableFuture<Long> nounceF = returnNextNonce(credentials.getAddress());
      final CompletableFuture<String> ctcAdrF = returnContractAddress();

      return CompletableFuture.allOf(ctcAdrF, nounceF).thenCompose(v -> {
         try {
            return withdraw(credentials, ctcAdrF.get(), amount, token, nounceF.get());
         } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
         }
      });
   }

   /**
    * Returns the lowest nonce that you can use from the given address in one of
    * the contract-backed trade functions.
    *
    * @param address
    *           Ethereum Address to get nounce for
    * @return CompletableFuture
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
    * Returns the best-priced open orders for a given market.
    *
    * The response is an object with asks and bids properties, each of which is
    * an array that contains orders sorted by best price (lowest ask first, and
    * highest bid first).
    *
    * By default, the asks and bids lists include 1 order each (ie. the lowest
    * ask and the highest bid). You may request up to 100 orders per segment by
    * including the count parameter. Pagination is not supported.
    *
    * Order objects contain price, amount, total, and orderHash properties, as
    * well as a params property which contains additional data about the order
    * that is useful for verifying the order's authenticity and to fill it. See
    * the trade API call below for details on how to fill orders.
    *
    * @param market
    *           Market pair
    * @param count
    *           Number of records to be returned per asks/bids segment.
    * @return Future
    */
   public CompletableFuture<OrderBook> returnOrderBook(final String market, final Integer count) {
      return process(ReturnOrderBook.create(market, count));
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
         final String market, final String address, final Integer count, final String cursor
   ) {
      return process(ReturnOpenOrders.create(market, address, count, cursor));
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
   public CompletableFuture<Page<List<Order>>> returnOpenOrdersPage(
         final String market, final String address, final Integer count, final String cursor
   ) {

      final Function<String, CompletableFuture<Page<List<Order>>>> valueProvider = nextCursor -> returnOpenOrdersPage(
            market, address, count, nextCursor);

      final BiFunction<List<Order>, String, Page<List<Order>>> pageBuilder = (results, nextCursor) -> {
         return new Page<>(nextCursor, valueProvider, results);
      };

      return process(ReturnOpenOrders.create(market, address, count, cursor), pageBuilder);
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
    * @return Future
    */
   public CompletableFuture<Page<List<Order>>> returnOpenOrdersPage(
         final String market, final String address, final Integer count
   ) {
      return returnOpenOrdersPage(market, address, count, null);
   }

   /**
    * Returns a single order.
    *
    * @param orderHash
    *           OrderHash
    * @return Future
    */
   public CompletableFuture<Order> returnOrderStatus(final String orderHash) {
      return process(ReturnOrderStatus.create(orderHash));
   }

   /**
    * Returns all trades involving a given order hash.
    *
    * @param orderHash
    *           The order hash to query for associated trades.
    * @return Future
    */
   public CompletableFuture<List<OrderTrade>> returnOrderTrades(final String orderHash) {
      return process(ReturnOrderTrades.create(orderHash));
   }

   /**
    * Returns a paginated list of all trades for a given market or address,
    * sorted by date.
    *
    * @param market
    *           Required if address not specified. Note market is separated with
    *           underscore. E.g ETH_ZCC, WBTC_OMG, TUSD_DAI
    * @param address
    *           Required if market not specified. Returns all trades that
    *           involve the given address as the maker or taker. Note - When
    *           querying by address, the type property of a trade refers to the
    *           action taken by the user, and not relative to the market. This
    *           behavior is designed to mirror the "My Trades" section of the
    *           IDEX website.
    * @param start
    *           Unix timestamp (in seconds) marking the time of the oldest trade
    *           that will be included.
    * @param end
    *           Unix timestamp (in seconds) marking the time of the newest trade
    *           that will be included.
    * @param sort
    *           Possible values are asc (oldest first) and desc (newest first).
    *           Defaults to desc.
    * @param count
    *           Number of records to be returned per request. [1..100]
    * @param cursor
    *           For pagination. Provide the value returned in the
    *           idex-next-cursor HTTP header to request the next slice (or
    *           page). This endpoint uses the tid property of a record for the
    *           cursor.
    * @return Future
    */
   public CompletableFuture<List<TradeHistory>> returnTradeHistory(
         String market, String address, Long start, Long end, SortOrder sort, Integer count, String cursor
   ) {
      return process(ReturnTradeHistory.create(market, address, start, end, sort, count, cursor));
   }

   /**
    * Returns Result Producer that paginats throught the results till no more is
    * found! All trades for a given market or address, sorted by date.
    *
    * @param market
    *           Required if address not specified. Note market is separated with
    *           underscore. E.g ETH_ZCC, WBTC_OMG, TUSD_DAI
    * @param address
    *           Required if market not specified. Returns all trades that
    *           involve the given address as the maker or taker. Note - When
    *           querying by address, the type property of a trade refers to the
    *           action taken by the user, and not relative to the market. This
    *           behavior is designed to mirror the "My Trades" section of the
    *           IDEX website.
    * @param start
    *           Unix timestamp (in seconds) marking the time of the oldest trade
    *           that will be included.
    * @param end
    *           Unix timestamp (in seconds) marking the time of the newest trade
    *           that will be included.
    * @param sortOrder
    *           Possible values are asc (oldest first) and desc (newest first).
    *           Defaults to desc.
    * @param count
    *           Number of records to be returned per request. [1..100]
    * @return Results Producer
    */
   public CompletableFuture<Page<List<TradeHistory>>> returnTradeHistoryPage(
         String market, String address, LocalDateTime start, LocalDateTime end, SortOrder sortOrder, Integer count
   ) {
      return returnTradeHistoryPage(market, address, start, end, sortOrder, count, null);
   }

   /**
    * Returns Result Producer that paginats throught the results till no more is
    * found! All trades for a given market or address, sorted by date.
    *
    * @param market
    *           Required if address not specified. Note market is separated with
    *           underscore. E.g ETH_ZCC, WBTC_OMG, TUSD_DAI
    * @param address
    *           Required if market not specified. Returns all trades that
    *           involve the given address as the maker or taker. Note - When
    *           querying by address, the type property of a trade refers to the
    *           action taken by the user, and not relative to the market. This
    *           behavior is designed to mirror the "My Trades" section of the
    *           IDEX website.
    * @param start
    *           Unix timestamp (in seconds) marking the time of the oldest trade
    *           that will be included.
    * @param end
    *           Unix timestamp (in seconds) marking the time of the newest trade
    *           that will be included.
    * @param sortOrder
    *           Possible values are asc (oldest first) and desc (newest first).
    *           Defaults to desc.
    * @param count
    *           Number of records to be returned per request. [1..100]
    * @param cursor
    *           For pagination. Provide the value returned in the
    *           idex-next-cursor HTTP header to request the next slice (or
    *           page). This endpoint uses the tid property of a record for the
    *           cursor.
    * @return Results Producer
    */
   public CompletableFuture<Page<List<TradeHistory>>> returnTradeHistoryPage(
         String market, String address, LocalDateTime start, LocalDateTime end, SortOrder sortOrder, Integer count,
         String cursor
   ) {

      final Function<String, CompletableFuture<Page<List<TradeHistory>>>> valueProvider = nextCursor -> returnTradeHistoryPage(
            market, address, start, end, sortOrder, count, nextCursor);

      final BiFunction<List<TradeHistory>, String, Page<List<TradeHistory>>> pageBuilder = (results, nextCursor) -> {
         return new Page<>(nextCursor, valueProvider, results);
      };

      return process(ReturnTradeHistory.create(market, address, Utils.toEpochSecond(start), Utils.toEpochSecond(end),
            sortOrder, count, cursor), pageBuilder);
   }

   /**
    * Returns a paginated list of all trades for a given market or address,
    * sorted by date.
    *
    * @param market
    *           Required if address not specified. Note market is separated with
    *           underscore. E.g ETH_ZCC, WBTC_OMG, TUSD_DAI
    * @param address
    *           Required if market not specified. Returns all trades that
    *           involve the given address as the maker or taker. Note - When
    *           querying by address, the type property of a trade refers to the
    *           action taken by the user, and not relative to the market. This
    *           behavior is designed to mirror the "My Trades" section of the
    *           IDEX website.
    * @param start
    *           Unix timestamp (in seconds) marking the time of the oldest trade
    *           that will be included.
    * @param end
    *           Unix timestamp (in seconds) marking the time of the newest trade
    *           that will be included.
    * @param sort
    *           Possible values are asc (oldest first) and desc (newest first).
    *           Defaults to desc.
    * @param count
    *           Number of records to be returned per request. [1..100]
    * @param cursor
    *           For pagination. Provide the value returned in the
    *           idex-next-cursor HTTP header to request the next slice (or
    *           page). This endpoint uses the tid property of a record for the
    *           cursor.
    * @return Future
    */
   public CompletableFuture<List<TradeHistory>> returnTradeHistory(
         String market, String address, LocalDateTime start, LocalDateTime end, SortOrder sort, Integer count,
         String cursor
   ) {
      return process(ReturnTradeHistory.create(market, address, Utils.toEpochSecond(start), Utils.toEpochSecond(end),
            sort, count, cursor));
   }

   /**
    * Returns your deposit and withdrawal history within a range, specified by
    * the "start" and "end" optional properties. Withdrawals can be marked as
    * "PENDING" if they are queued for dispatch, "PROCESSING" if the transaction
    * has been dispatched, and "COMPLETE" if the transaction has been mined.
    *
    * @param address
    *           Address of the wallet
    * @param start
    *           Optional Inclusive starting UNIX timestamp of returned result
    * @param end
    *           Optional Inclusive ending UNIX timestamp of returned results.
    *           Defaults to current timestamp
    * @return CompletableFuture
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
    * Returns all the currency pair availble on the IDEX Market.
    *
    * @return CurrencyPairs
    */
   public CompletableFuture<CurrencyPairs> returnCurrenciesWithPairs() {
      return process(ReturnCurrenciesWithPairs.create());
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
      return process(ReturnTicker.create(market));
   }

   /**
    * Shutdowns the client and closes all connections.
    *
    * @throws IOException
    */
   public void shutdown() throws IOException {
      if (client.isClosed())
         return;
      client.close();
   }

   private <V, T extends Parser<V> & Req> CompletableFuture<V> process(final T requestParser) {
      return sendAsync(requestParser).thenApply(httpRsp -> {
         return httpRsp.getResponseBody();
      }).thenApply(body -> {
         Utils.prettyPrint(mapper, body);
         return body;
      }).thenApply(body -> requestParser.parse(mapper, body));
   }

   private <V, T extends Parser<V> & Req> CompletableFuture<Page<V>> process(
         final T requestParser, final BiFunction<V, String, Page<V>> pageProducer
   ) {
      return sendAsync(requestParser).thenApply(httpRsp -> {
         final String body = httpRsp.getResponseBody();
         if (log.isDebugEnabled())
            log.debug("process: " + Utils.prettyfyJson(mapper, body));
         return pageProducer.apply(requestParser.parse(mapper, body), httpRsp.getHeader("idex-next-cursor"));
      });
   }

   private CompletableFuture<Response> sendAsync(final Req req) {
      if (log.isDebugEnabled()) {
         log.debug("sendAsync : " + req);
         log.debug("sendAsync : " + req.getPayload());
         log.debug(Utils.prettyfyJson(mapper, req.getPayload()));
      }
      final Request httpreq = new RequestBuilder(HttpConstants.Methods.POST).setUrl(HTTP_ENDPOINT + req.getEndpoint())
            .setHeader("Content-Type", CONTENT_TYPE).setBody(req.getPayload()).build();
      return client.executeRequest(httpreq).toCompletableFuture();
   }

   @Override
   public void close() throws IOException {
      shutdown();
   }
}
