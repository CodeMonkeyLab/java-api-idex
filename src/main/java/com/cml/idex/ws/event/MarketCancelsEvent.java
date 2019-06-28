package com.cml.idex.ws.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.cml.idex.ws.Category.Markets;
import com.cml.idex.ws.EventType;
import com.cml.idex.ws.value.Cancel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides an aggregated group of new cancels which have occurred on the
 * subscribed market.
 *
 * @author codemonkeylab
 *
 */
public class MarketCancelsEvent extends Event<Markets> {

   private static final Logger            log             = LoggerFactory.getLogger(MarketCancelsEvent.class);
   public static final EventType<Markets> EVENT_TYPE      = EventType.MARKET_CANCELS;
   public static final String             EVENT_TYPE_NAME = "market_cancels";

   final String                           market;
   final List<Cancel>                     cancels;

   private MarketCancelsEvent(String chain, long seqID, String eventID, String market, List<Cancel> cancels) {
      super(chain, seqID, eventID);
      this.market = market;
      this.cancels = cancels;
   }

   /**
    * Market the cancel(s) are for.
    *
    * @return Market
    */
   public String getMarket() {
      return market;
   }

   /**
    * Returns List or Cancel(s).
    *
    * @return Cancel(s)
    */
   public List<Cancel> getCancels() {
      return cancels;
   }

   @Override
   public EventType<Markets> getEventType() {
      return EVENT_TYPE;
   }

   @Override
   public String toString() {
      return "MarketCancels [market=" + market + ", cancels=" + cancels + ", chain=" + chain + ", seqID=" + seqID
            + ", eventID=" + eventID + "]";
   }

   public static MarketCancelsEvent parse(final ObjectMapper mapper, final JsonNode root) {
      try {
         final String chain = root.get("chain").asText();
         final String eid = root.get("eid").asText();
         final long seqID = root.get("seq").asLong();

         final JsonNode payload = mapper.readTree(root.get("payload").asText());
         final String market = payload.get("market").asText();

         final List<Cancel> cancels = new LinkedList<>();
         final Iterator<JsonNode> orderItr = payload.get("cancels").elements();
         while (orderItr.hasNext())
            cancels.add(Cancel.parseOrder(orderItr.next()));

         return new MarketCancelsEvent(chain, seqID, eid, market, cancels);
      } catch (Throwable e) {
         log.error("Error parsing MarketCancelsEvent!");
         log.error(Utils.prettyfyJson(mapper, root.toString()));
         log.error(e.getLocalizedMessage(), e);
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e.getLocalizedMessage(), e);
      }
   }

}
