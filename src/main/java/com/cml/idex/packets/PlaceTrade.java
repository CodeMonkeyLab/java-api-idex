package com.cml.idex.packets;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.cml.idex.value.Trade;
import com.cml.idex.value.TradeResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PlaceTrade implements Req, Parser<List<TradeResponse>> {

   final List<Trade> trades;

   private PlaceTrade(List<Trade> trades) {
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
   public List<TradeResponse> parse(ObjectMapper mapper, String body) {
      if (Utils.isEmptyJson(body))
         throw new IDexException(ErrorCode.TRADE_FAILED, body);
      return fromJson(mapper, body);
   }

   public static PlaceTrade create(List<Trade> trades) {
      return new PlaceTrade(trades);
   }

   private static List<TradeResponse> fromJson(final ObjectMapper mapper, final String body) {
      try {
         final JsonNode root = mapper.readTree(body);
         if (root.isArray()) {
            List<TradeResponse> trades = new LinkedList<>();
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

   public static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

   public static TradeResponse parseTrade(final JsonNode node) {
      if (node == null)
         return null;

      final BigDecimal amount = Utils.toBD(node, "amount");
      final LocalDateTime date = LocalDateTime.parse(node.get("date").asText(), DT_FORMATTER);
      final BigDecimal total = Utils.toBD(node, "total");
      final String market = node.get("market").asText();
      final String type = node.get("type").asText();
      final BigDecimal price = Utils.toBD(node, "price");
      final String orderHash = node.get("orderHash").asText();
      final String uuid = node.get("uuid").asText();

      return new TradeResponse(amount, date, total, market, type, price, orderHash, uuid);
   }

}
