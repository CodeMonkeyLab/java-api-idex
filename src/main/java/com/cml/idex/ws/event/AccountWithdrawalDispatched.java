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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A withdrawal request is first dispatched to the blockchain.
 *
 * At this point the withdrawal should be considered as confirming
 *
 * TIP: At this point you will receive the associated transactionHash since it
 * has been sent to the blockchain for processing.
 *
 * @author zcode.dev
 *
 */
public class AccountWithdrawalDispatched extends Event<Accounts> {

   private static final Logger             log             = LoggerFactory.getLogger(AccountWithdrawalDispatched.class);

   public static final EventType<Accounts> EVENT_TYPE      = EventType.ACCOUNT_TRADES;
   public static final String              EVENT_TYPE_NAME = "account_withdrawal_dispatched";

   final List<String>                      accounts;
   final String                            sender;
   final long                              id;
   final String                            transactionHash;

   public AccountWithdrawalDispatched(
         String chain, long seqID, String eventID, List<String> accounts, String sender, long id, String transactionHash
   ) {
      super(chain, seqID, eventID);
      this.accounts = accounts;
      this.sender = sender;
      this.id = id;
      this.transactionHash = transactionHash;
   }

   /**
    * The accounts address the withdrawal are for.
    *
    * @return List of Accounts
    */
   public List<String> getAccounts() {
      return accounts;
   }

   public String getSender() {
      return sender;
   }

   public long getId() {
      return id;
   }

   public String getTransactionHash() {
      return transactionHash;
   }

   @Override
   public EventType<Accounts> getEventType() {
      return EVENT_TYPE;
   }

   @Override
   public String toString() {
      return "AccountWithdrawalDispatched [accounts=" + accounts + ", sender=" + sender + ", id=" + id
            + ", transactionHash=" + transactionHash + "]";
   }

   public static AccountWithdrawalDispatched parse(final ObjectMapper mapper, final JsonNode root) {
      try {
         final String chain = Optional.of(root.get("chain")).map(JsonNode::asText).orElse(null);
         final String eid = root.get("eid").asText();
         final long seqID = root.get("seq").asLong();

         final JsonNode payload = mapper.readTree(root.get("payload").asText());

         final List<String> accounts = new LinkedList<>();
         final Iterator<JsonNode> accountItr = payload.get("account").elements();
         while (accountItr.hasNext())
            accounts.add(accountItr.next().asText());

         JsonNode node = payload.get("withdrawal");

         final String sender = node.get("sender").asText();
         final long id = node.get("id").asLong();
         final String transactionHash = node.get("transactionHash").asText();

         return new AccountWithdrawalDispatched(chain, seqID, eid, accounts, sender, id, transactionHash);
      } catch (Throwable e) {
         log.error("Error parsing AccountWithdrawalDispatched!");
         log.error(Utils.prettyfyJson(mapper, root.toString()));
         log.error(e.getLocalizedMessage(), e);
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e.getLocalizedMessage(), e);
      }
   }

}
