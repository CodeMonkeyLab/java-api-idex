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
import com.cml.idex.ws.value.CancelMarket;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides an aggregated group of new cancels which have occurred on the
 * subscribed market.
 *
 * @author codemonkeylab
 *
 */
public class AccountCancelsEvent extends Event<Accounts> {

   private static final Logger             log             = LoggerFactory.getLogger(AccountCancelsEvent.class);

   public static final EventType<Accounts> EVENT_TYPE      = EventType.ACCOUNT_CANCELS;
   public static final String              EVENT_TYPE_NAME = "account_cancels";

   final String                            account;
   final List<CancelMarket>                cancels;

   public AccountCancelsEvent(String chain, long seqID, String eventID, String account, List<CancelMarket> cancels) {
      super(chain, seqID, eventID);
      this.account = account;
      this.cancels = cancels;
   }

   /**
    * The account address the order(s) are for.
    *
    * @return Market
    */
   public String getAccount() {
      return account;
   }

   /**
    * Returns List or Cancel(s).
    *
    * @return Cancel(s)
    */
   public List<CancelMarket> getCancels() {
      return cancels;
   }

   @Override
   public EventType<Accounts> getEventType() {
      return EVENT_TYPE;
   }

   @Override
   public String toString() {
      return "AccountCancelsEvent [account=" + account + ", chain=" + chain + ", seqID=" + seqID + ", eventID="
            + eventID + "]";
   }

   public static AccountCancelsEvent parse(final ObjectMapper mapper, final JsonNode root) {
      try {
         final String chain = Optional.of(root.get("chain")).map(JsonNode::asText).orElse(null);
         final String eid = root.get("eid").asText();
         final long seqID = root.get("seq").asLong();

         final JsonNode payload = mapper.readTree(root.get("payload").asText());
         final String account = payload.get("account").asText();

         final List<CancelMarket> cancels = new LinkedList<>();
         final Iterator<JsonNode> orderItr = payload.get("cancels").elements();
         while (orderItr.hasNext())
            cancels.add(CancelMarket.parseOrder(orderItr.next()));

         return new AccountCancelsEvent(chain, seqID, eid, account, cancels);
      } catch (Throwable e) {
         log.error("Error parsing AccountCancelsEvent!");
         log.error(Utils.prettyfyJson(mapper, root.toString()));
         log.error(e.getLocalizedMessage(), e);
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e.getLocalizedMessage(), e);
      }
   }

}
