package com.cml.idex.ws.event;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.cml.idex.ws.Category.Markets;
import com.cml.idex.ws.EventType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MarketListingEvent extends Event<Markets> {

   private static final Logger            log             = LoggerFactory.getLogger(MarketListingEvent.class);
   public static final EventType<Markets> EVENT_TYPE      = EventType.MARKET_LISTING;
   public static final String             EVENT_TYPE_NAME = "market_listing";

   final Action                           action;
   final String                           market;
   final String                           to;

   public MarketListingEvent(String chain, long seqID, String eventID, Action action, String market, String to) {
      super(chain, seqID, eventID);
      this.action = action;
      this.market = market;
      this.to = to;
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
    * Listing action.
    *
    * @return Action
    */
   public Action getAction() {
      return action;
   }

   /**
    * Used for Action.renamed
    *
    * @return Renamed market name
    */
   public String getTo() {
      return to;
   }

   @Override
   public EventType<Markets> getEventType() {
      return EVENT_TYPE;
   }

   @Override
   public String toString() {
      return "MarketListingEvent [action=" + action + ", market=" + market + ", to=" + to + "]";
   }

   public static MarketListingEvent parse(final ObjectMapper mapper, final JsonNode root) {
      try {
         final String chain = Optional.of(root.get("chain")).map(JsonNode::asText).orElse(null);
         final String eid = root.get("eid").asText();
         final long seqID = root.get("seq").asLong();

         final JsonNode payload = mapper.readTree(root.get("payload").asText());
         final Action action = Action.fromString(payload.get("action").asText());
         final String market = payload.get("market").asText();
         final String to = payload.get("to").asText();

         return new MarketListingEvent(chain, seqID, eid, action, market, to);
      } catch (Throwable e) {
         log.error("Error parsing MarketListingEvent!");
         log.error(Utils.prettyfyJson(mapper, root.toString()));
         log.error(e.getLocalizedMessage(), e);
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e.getLocalizedMessage(), e);
      }
   }

   public enum Action {
      listed,
      delisted,
      renamed;

      public static Action fromString(final String value) {
         if (value == null)
            return null;

         for (Action act : values()) {
            if (act.name().equalsIgnoreCase(value))
               return act;
         }
         return null;
      }
   }
}
