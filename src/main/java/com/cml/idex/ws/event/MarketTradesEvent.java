package com.cml.idex.ws.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.cml.idex.ws.Category.Markets;
import com.cml.idex.ws.EventType;
import com.cml.idex.ws.value.Trade;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MarketTradesEvent extends Event<Markets> {

   private static final Logger            log             = LoggerFactory.getLogger(MarketTradesEvent.class);
   public static final EventType<Markets> EVENT_TYPE      = EventType.MARKET_TRADES;
   public static final String             EVENT_TYPE_NAME = "market_trades";

   final String                           market;
   final long                             total;
   final long                             highestTimestamp;
   final List<Trade>                      trades;

   public MarketTradesEvent(
         String chain, long seqID, String eventID, String market, long total, long highestTimestamp, List<Trade> trades
   ) {
      super(chain, seqID, eventID);
      this.market = market;
      this.total = total;
      this.highestTimestamp = highestTimestamp;
      this.trades = trades;
   }

   /**
    * Market the order(s) are for.
    *
    * @return Market
    */
   public String getMarket() {
      return market;
   }

   /**
    * Total Trades
    *
    * @return Trade Count
    */
   public long getTotal() {
      return total;
   }

   /**
    * Biggest Timestamp of all the trades. Unix epoch time. To convert to
    * TimeStamp remember to multiply with 1000.
    *
    * @return Timestamp
    */
   public long getHighestTimestamp() {
      return highestTimestamp;
   }

   /**
    * Returns List or Trade(s).
    *
    * @return Trade(s)
    */
   public List<Trade> getTrades() {
      return trades;
   }

   @Override
   public EventType<Markets> getEventType() {
      return EVENT_TYPE;
   }

   @Override
   public String toString() {
      return "MarketTradesEvent [market=" + market + ", total=" + total + ", highestTimestamp=" + highestTimestamp
            + ", chain=" + chain + ", seqID=" + seqID + ", eventID=" + eventID + "]";
   }

   public static MarketTradesEvent parse(final ObjectMapper mapper, final JsonNode root) {
      try {
         final String chain = Optional.of(root.get("chain")).map(JsonNode::asText).orElse(null);
         final String eid = root.get("eid").asText();
         final long seqID = root.get("seq").asLong();

         final JsonNode payload = mapper.readTree(root.get("payload").asText());
         final String market = payload.get("market").asText();
         final long total = payload.get("total").asLong();
         final long highestTimestamp = payload.get("highestTimestamp").asLong();

         final List<Trade> trades = new LinkedList<>();
         final Iterator<JsonNode> orderItr = payload.get("trades").elements();
         while (orderItr.hasNext())
            trades.add(Trade.parseOrder(orderItr.next()));

         return new MarketTradesEvent(chain, seqID, eid, market, total, highestTimestamp, trades);
      } catch (Throwable e) {
         log.error("Error parsing MarketTradesEvent!");
         log.error(Utils.prettyfyJson(mapper, root.toString()));
         log.error(e.getLocalizedMessage(), e);
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e.getLocalizedMessage(), e);
      }
   }
}
