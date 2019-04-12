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
import com.cml.idex.value.TradeHistory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReturnTradeHistory implements Req, Parser<List<TradeHistory>> {

   private final String    market;
   private final String    address;
   private final Long      start;
   private final Long      end;
   private final SortOrder sort;
   private final Integer   count;
   private final String    cursor;

   private ReturnTradeHistory(
         String market, String address, Long start, Long end, SortOrder sort, Integer count, String cursor
   ) {
      super();
      this.market = market;
      this.address = address;
      this.start = start;
      this.end = end;
      this.sort = sort;
      this.count = count;
      this.cursor = cursor;
   }

   @Override
   public String getEndpoint() {
      return "returnTradeHistory";
   }

   @Override
   public String getPayload() {
      StringBuilder sb = new StringBuilder();
      sb.append("{");
      if (market != null)
         sb.append("\"market\":\"").append(market).append("\"");
      if (address != null) {
         if (market != null)
            sb.append(",");
         sb.append("\"address\":\"").append(address).append("\"");
      }
      if (start != null)
         sb.append(",\"start\":").append(start.longValue());
      if (end != null)
         sb.append(",\"end\":").append(end.longValue());
      if (sort != null)
         sb.append(",\"sort\": \"").append(sort.getValue()).append("\"");
      if (count != null)
         sb.append(",\"count\":").append(count);
      if (cursor != null)
         sb.append(",\"cursor\": \"").append(cursor).append("\"");
      return sb.append("}").toString();
   }

   @Override
   public List<TradeHistory> parse(ObjectMapper mapper, String json) {
      if (Utils.isEmptyJson(json))
         return Collections.emptyList();
      return fromJson(mapper, json);
   }

   public static ReturnTradeHistory create(
         String market, String address, Long start, Long end, SortOrder sort, Integer count, String cursor
   ) {
      final String marketFixed = Utils.fixString(market);
      final String adrFixed = Utils.fixString(address);

      if (marketFixed == null && adrFixed == null)
         throw new IllegalArgumentException("market or address is requried!");

      if (count != null)
         if (count < 1 || count > 100)
            throw new IllegalArgumentException("count must be between 1 and 100 OR null value");

      return new ReturnTradeHistory(marketFixed, address, start, end, sort, count, cursor);
   }

   private static List<TradeHistory> fromJson(final ObjectMapper mapper, final String json) {
      try {
         final JsonNode root = mapper.readTree(json);
         Utils.checkError(root);

         final List<TradeHistory> trades = new LinkedList<>();

         final Iterator<JsonNode> eleItr = root.elements();
         while (eleItr.hasNext()) {
            trades.add(parseTradeHistory(eleItr.next()));
         }
         return trades;
      } catch (Exception e1) {
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e1.getLocalizedMessage(), e1);
      }
   }

   private static TradeHistory parseTradeHistory(final JsonNode node) {
      final LocalDateTime date = Utils.parseDate(node, "date");
      final BigDecimal amount = Utils.toBDrequired(node, "amount");
      final String type = node.get("type").asText();
      final BigDecimal total = Utils.toBDrequired(node, "total");
      final BigDecimal price = Utils.toBDrequired(node, "price");
      final String orderHash = node.get("orderHash").asText();
      final String uuid = node.get("uuid").asText();
      final Long tid = node.get("tid").asLong();
      final BigDecimal buyerFee = Utils.toBDrequired(node, "buyerFee");
      final BigDecimal sellerFee = Utils.toBDrequired(node, "sellerFee");
      final BigDecimal gasFee = Utils.toBDrequired(node, "gasFee");
      final Long timestamp = node.get("timestamp").asLong();
      final String maker = node.get("maker").asText();
      final String taker = node.get("taker").asText();
      final String tokenBuy = node.get("tokenBuy").asText();
      final String tokenSell = node.get("tokenSell").asText();
      final String transactionHash = node.get("transactionHash").asText();
      final String usdValue = node.get("usdValue").asText();

      return new TradeHistory(date, amount, type, total, price, orderHash, uuid, tid, buyerFee, sellerFee, gasFee,
            timestamp, maker, taker, tokenBuy, tokenSell, transactionHash, usdValue);
   }

}
