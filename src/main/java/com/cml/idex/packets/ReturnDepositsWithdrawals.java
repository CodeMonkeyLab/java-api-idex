package com.cml.idex.packets;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.cml.idex.value.DepositHistory;
import com.cml.idex.value.DepositsWithdrawals;
import com.cml.idex.value.WithdrawHistory;
import com.cml.idex.value.WithdrawStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Returns your deposit and withdrawal history within a range, specified by the
 * "start" and "end" properties of the JSON input, both of which must be UNIX
 * timestamps. Withdrawals can be marked as "PENDING" if they are queued for
 * dispatch, "PROCESSING" if the transaction has been dispatched, and "COMPLETE"
 * if the transaction has been mined.
 * 
 * @author plouw
 *
 */
public class ReturnDepositsWithdrawals implements Req, Parser<DepositsWithdrawals> {

   private final String address;
   private final Long   start;
   private final Long   end;

   private ReturnDepositsWithdrawals(String address, LocalDateTime start, LocalDateTime end) {
      super();
      this.address = address;
      this.start = start == null ? null : Utils.toEpochSecond(start);
      this.end = end == null ? null : Utils.toEpochSecond(end);
   }

   @Override
   public String getEndpoint() {
      return "returnDepositsWithdrawals";
   }

   @Override
   public String getPayload() {
      final StringBuilder sb = new StringBuilder("{\"address\":\"" + address + "\"");
      if (start != null)
         sb.append(",\"start\":").append(start.longValue());
      if (end != null)
         sb.append(",\"end\":").append(end.longValue());
      return sb.append("}").toString();
   }

   @Override
   public DepositsWithdrawals parse(ObjectMapper mapper, String json) {
      if (Utils.isEmptyJson(json))
         throw new IDexException(ErrorCode.UNKNOWN_ADDRESS, address);
      return fromJson(mapper, json);
   }

   /**
    * returnDepositsWithdrawals
    * 
    * @param address
    *           Address of the wallet
    * @param start
    *           Optional Inclusive starting UNIX timestamp of returned result
    * @param end
    *           Optional Inclusive ending UNIX timestamp of returned results.
    *           Defaults to current timestamp
    * @return ReturnDepositsWithdrawals
    */
   public static ReturnDepositsWithdrawals create(
         final String address, final LocalDateTime start, final LocalDateTime end
   ) {
      final String adrFixed = Utils.fixString(address);
      if (adrFixed == null)
         throw new IllegalArgumentException("address is required");

      return new ReturnDepositsWithdrawals(adrFixed, start, end);
   }

   private static DepositsWithdrawals fromJson(final ObjectMapper mapper, final String body) {
      try {
         final JsonNode root = mapper.readTree(body);
         Utils.checkError(root);

         List<DepositHistory> deposits = parseDeposits(root.get("deposits"));
         List<WithdrawHistory> withdrawals = parseWithdrawals(root.get("withdrawals"));

         return new DepositsWithdrawals(deposits, withdrawals);
      } catch (Exception e1) {
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e1.getLocalizedMessage(), e1);
      }
   }

   private static List<WithdrawHistory> parseWithdrawals(final JsonNode node) {
      if (node == null)
         return Collections.emptyList();

      ArrayNode withdrawArr = (ArrayNode) node;
      Iterator<JsonNode> elementItr = withdrawArr.elements();

      List<WithdrawHistory> deposits = new LinkedList<>();

      while (elementItr.hasNext()) {
         JsonNode depNode = elementItr.next();
         int withdrawalNumber = depNode.get("withdrawalNumber").asInt();
         String currency = depNode.get("currency").asText();
         BigDecimal amount = Utils.toBDrequired(depNode, "amount");
         long timestamp = depNode.get("timestamp").asLong();
         String transactionHash = depNode.get("transactionHash").asText();
         WithdrawStatus status = WithdrawStatus.fromString(depNode.get("status").asText());
         deposits.add(new WithdrawHistory(withdrawalNumber, currency, amount, timestamp, transactionHash, status));
      }

      return deposits;
   }

   private static List<DepositHistory> parseDeposits(final JsonNode node) {
      if (node == null)
         return Collections.emptyList();

      ArrayNode depArr = (ArrayNode) node;
      Iterator<JsonNode> elementItr = depArr.elements();

      List<DepositHistory> deposits = new LinkedList<>();

      while (elementItr.hasNext()) {
         JsonNode depNode = elementItr.next();
         int depositNumber = depNode.get("depositNumber").asInt();
         String currency = depNode.get("currency").asText();
         BigDecimal amount = Utils.toBDrequired(depNode, "amount");
         long timestamp = depNode.get("timestamp").asLong();
         String transactionHash = depNode.get("transactionHash").asText();
         deposits.add(new DepositHistory(depositNumber, currency, amount, timestamp, transactionHash));
      }

      return deposits;
   }
}
