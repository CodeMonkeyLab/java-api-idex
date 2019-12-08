package com.cml.idex.ws.event;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.cml.idex.ws.Category.Accounts;
import com.cml.idex.ws.EventType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Received whenever the subscribed accounts nonce has been updated. This new
 * nonce must be used as the base nonce for all future requests.
 *
 * @author codemonkeylab
 *
 */
public class AccountNonceEvent extends Event<Accounts> {

   private static final Logger             log             = LoggerFactory.getLogger(AccountNonceEvent.class);

   public static final EventType<Accounts> EVENT_TYPE      = EventType.ACCOUNT_NONCE;
   public static final String              EVENT_TYPE_NAME = "account_nonce";

   final String                            account;
   final long                              nonce;

   public AccountNonceEvent(String chain, long seqID, String eventID, String account, long nonce) {
      super(chain, seqID, eventID);
      this.account = account;
      this.nonce = nonce;
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
    * Next nonce.
    *
    * @return Nonce
    */
   public long getNonce() {
      return nonce;
   }

   @Override
   public EventType<Accounts> getEventType() {
      return EVENT_TYPE;
   }

   @Override
   public String toString() {
      return "AccountNonceEvent [account=" + account + ", nonce=" + nonce + "]";
   }

   public static AccountNonceEvent parse(final ObjectMapper mapper, final JsonNode root) {
      try {
         final String chain = Optional.of(root.get("chain")).map(JsonNode::asText).orElse(null);
         final String eid = root.get("eid").asText();
         final long seqID = root.get("seq").asLong();

         final JsonNode payload = mapper.readTree(root.get("payload").asText());
         final String account = payload.get("account").asText();
         final long nonce = payload.get("nonce").asLong();

         return new AccountNonceEvent(chain, seqID, eid, account, nonce);
      } catch (Throwable e) {
         log.error("Error parsing AccountNonceEvent!");
         log.error(Utils.prettyfyJson(mapper, root.toString()));
         log.error(e.getLocalizedMessage(), e);
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e.getLocalizedMessage(), e);
      }
   }

}
