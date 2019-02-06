package com.cml.idex.packets;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.cml.idex.value.TradeReq;
import com.cml.idex.value.Trade;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PlaceTrade implements Req, Parser<List<Trade>> {

   final List<TradeReq> trades;

   private PlaceTrade(List<TradeReq> trades) {
      super();
      this.trades = trades;
   }

   @Override
   public String getEndpoint() {
      return "trade";
   }

   @Override
   public String getPayload() {
      String tradesJson = trades.stream()
            .map(trade -> new StringBuilder().append("{\"address\": \"").append(trade.getAddress())
                  .append("\", \"amount\": \"").append(trade.getAmount()).append("\", \"orderHash\": \"")
                  .append(trade.getOrderHash()).append("\", \"nonce\": \"").append(trade.getNonce()).append(", \"v\": ")
                  .append(trade.getV()).append(", \"r\": \"").append(new String(trade.getR(), StandardCharsets.UTF_8))
                  .append("\", \"s\": \"").append(new String(trade.getS(), StandardCharsets.UTF_8)).append("}")
                  .toString())
            .collect(Collectors.joining(","));
      ;

      return "{[" + tradesJson + "]}";
   }

   @Override
   public List<Trade> parse(ObjectMapper mapper, String json) {
      if (Utils.isEmptyJson(json))
         throw new IDexException(ErrorCode.TRADE_FAILED, json);
      return fromJson(mapper, json);
   }

   public static PlaceTrade create(List<TradeReq> trades) {
      return new PlaceTrade(trades);
   }

   private static List<Trade> fromJson(final ObjectMapper mapper, final String body) {
      try {
         final JsonNode root = mapper.readTree(body);
         Utils.checkError(root);
         if (root.isArray()) {
            List<Trade> trades = new LinkedList<>();
            Iterator<JsonNode> elementsItr = root.elements();
            while (elementsItr.hasNext()) {
               trades.add(parseTrade(elementsItr.next()));
            }
            return trades;
         }

         if (root.get("amount") == null)
            throw new IDexException(ErrorCode.TRADE_FAILED, body);
         return Collections.singletonList(parseTrade(root));
      } catch (Exception e1) {
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e1.getLocalizedMessage(), e1);
      }
   }

   public static Trade parseTrade(final JsonNode node) {
      if (node == null)
         return null;

      final BigDecimal amount = Utils.toBDrequired(node, "amount");
      final LocalDateTime date = Utils.parseDate(node, "date");
      final BigDecimal total = Utils.toBDrequired(node, "total");
      final String market = node.get("market").asText();
      final String type = node.get("type").asText();
      final BigDecimal price = Utils.toBDrequired(node, "price");
      final String orderHash = node.get("orderHash").asText();
      final String uuid = node.get("uuid").asText();

      return new Trade(amount, date, total, market, type, price, orderHash, uuid);
   }

}
