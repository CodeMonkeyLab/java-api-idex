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
import com.cml.idex.ws.Category.Accounts;
import com.cml.idex.ws.EventType;
import com.cml.idex.ws.value.Trade;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * When the subscribed account has new trades received and processed by the
 * exchange, this event will be provided including each of the trades that were
 * processed in the given batch.
 *
 * At this point the trades should be considered pending.
 *
 * @author codemonkeylab
 *
 */
public class AccountTradesEvent extends Event<Accounts> {

   private static final Logger             log             = LoggerFactory.getLogger(AccountTradesEvent.class);

   public static final EventType<Accounts> EVENT_TYPE      = EventType.ACCOUNT_TRADES;
   public static final String              EVENT_TYPE_NAME = "account_trades";

   final String                            account;
   final List<Trade>                       trades;

   final long                              total;
   final long                              highestTimestamp;

   public AccountTradesEvent(
         String chain, long seqID, String eventID, String account, long total, long highestTimestamp, List<Trade> trades
   ) {
      super(chain, seqID, eventID);
      this.account = account;
      this.total = total;
      this.highestTimestamp = highestTimestamp;
      this.trades = trades;
   }

   /**
    * The account address the trade(s) are for.
    *
    * @return Market
    */
   public String getAccount() {
      return account;
   }

   /**
    * Returns List or Trade(s).
    *
    * @return Trade(s)
    */
   public List<Trade> getTrades() {
      return trades;
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

   @Override
   public EventType<Accounts> getEventType() {
      return EVENT_TYPE;
   }

   @Override
   public String toString() {
      return "AccountTradesEvent [account=" + account + ", total=" + total + ", highestTimestamp=" + highestTimestamp
            + ", chain=" + chain + ", seqID=" + seqID + ", eventID=" + eventID + "]";
   }

   public static AccountTradesEvent parse(final ObjectMapper mapper, final JsonNode root) {
      try {
         final String chain = Optional.of(root.get("chain")).map(JsonNode::asText).orElse(null);
         final String eid = root.get("eid").asText();
         final long seqID = root.get("seq").asLong();

         final JsonNode payload = mapper.readTree(root.get("payload").asText());
         final String account = payload.get("account").asText();
         final long total = payload.get("total").asLong();
         final long highestTimestamp = payload.get("highestTimestamp").asLong();

         final List<Trade> trades = new LinkedList<>();
         final Iterator<JsonNode> orderItr = payload.get("trades").elements();
         while (orderItr.hasNext())
            trades.add(Trade.parseOrder(orderItr.next()));

         return new AccountTradesEvent(chain, seqID, eid, account, total, highestTimestamp, trades);
      } catch (Throwable e) {
         log.error("Error parsing AccountTradesEvent!");
         log.error(Utils.prettyfyJson(mapper, root.toString()));
         log.error(e.getLocalizedMessage(), e);
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e.getLocalizedMessage(), e);
      }
   }

}
