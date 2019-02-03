package com.cml.idex.packets;

import java.math.BigDecimal;
import java.util.Objects;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.cml.idex.value.Ticker;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReturnTicker implements Req, Parser<Ticker> {

   public static final String ENDPOINT = "returnTicker";

   final String               market;

   private ReturnTicker(String market) {
      super();
      this.market = market;
   }

   @Override
   public String getEndpoint() {
      return ENDPOINT;
   }

   @Override
   public String getPayload() {
      return market == null ? "" : "{\"market\": \"" + market + "\"}";
   }

   public static final ReturnTicker createAll() {
      return new ReturnTicker(null);
   }

   public static final ReturnTicker create(final String market) {
      Objects.requireNonNull(market);
      return new ReturnTicker(market);
   }

   @Override
   public Ticker parse(ObjectMapper mapper, String body) {
      return fromJson(mapper, body, market);
   }

   public static Ticker fromJson(final ObjectMapper mapper, final String body, final String market) {

      try {
         if (!body.contains("last"))
            throw new IDexException(ErrorCode.UNKNOW_MARKET, market);

         final JsonNode root = mapper.readTree(body);

         BigDecimal last = Utils.toBD(root.get("last").asText());
         BigDecimal high = Utils.toBD(root.get("high").asText());
         BigDecimal low = Utils.toBD(root.get("low").asText());
         BigDecimal lowestAsk = Utils.toBD(root.get("lowestAsk").asText());
         BigDecimal highestBid = Utils.toBD(root.get("highestBid").asText());
         BigDecimal percentChange = Utils.toBD(root.get("percentChange").asText());
         BigDecimal baseVolume = Utils.toBD(root.get("baseVolume").asText());
         BigDecimal quoteVolume = Utils.toBD(root.get("quoteVolume").asText());

         return new Ticker(last, high, low, lowestAsk, highestBid, percentChange, baseVolume, quoteVolume);
      } catch (Exception e1) {
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e1.getLocalizedMessage(), e1);
      }
   }
}
