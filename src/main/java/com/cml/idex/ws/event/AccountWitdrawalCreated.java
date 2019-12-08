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
import com.cml.idex.ws.value.Withdrawal;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A withdrawal request is first received by the server and queued to be
 * dispatched to the blockchain.
 *
 * At this point the withdrawal should be considered as pending.
 *
 * @author zcode.dev
 *
 */
public class AccountWitdrawalCreated extends Event<Accounts> {

   private static final Logger             log             = LoggerFactory.getLogger(AccountWitdrawalCreated.class);

   public static final EventType<Accounts> EVENT_TYPE      = EventType.ACCOUNT_TRADES;
   public static final String              EVENT_TYPE_NAME = "account_withdrawal_created";

   final List<String>                      accounts;
   final Withdrawal                        withdrawal;

   public AccountWitdrawalCreated(
         String chain, long seqID, String eventID, List<String> accounts, Withdrawal withdrawal
   ) {
      super(chain, seqID, eventID);
      this.accounts = accounts;
      this.withdrawal = withdrawal;
   }

   /**
    * The accounts address the withdrawal are for.
    *
    * @return List of Accounts
    */
   public List<String> getAccounts() {
      return accounts;
   }

   public Withdrawal getWithdrawal() {
      return withdrawal;
   }

   @Override
   public EventType<Accounts> getEventType() {
      return EVENT_TYPE;
   }

   @Override
   public String toString() {
      return "AccountWitdrawalCreated [accounts=" + accounts + ", withdrawal=" + withdrawal + "]";
   }

   public static AccountWitdrawalCreated parse(final ObjectMapper mapper, final JsonNode root) {
      try {
         final String chain = Optional.of(root.get("chain")).map(JsonNode::asText).orElse(null);
         final String eid = root.get("eid").asText();
         final long seqID = root.get("seq").asLong();

         final JsonNode payload = mapper.readTree(root.get("payload").asText());

         final List<String> accounts = new LinkedList<>();
         final Iterator<JsonNode> accountItr = payload.get("account").elements();
         while (accountItr.hasNext())
            accounts.add(accountItr.next().asText());

         Withdrawal withdrawal = Withdrawal.parse(payload.get("withdrawal"));

         return new AccountWitdrawalCreated(chain, seqID, eid, accounts, withdrawal);
      } catch (Throwable e) {
         log.error("Error parsing AccountWitdrawalCreated!");
         log.error(Utils.prettyfyJson(mapper, root.toString()));
         log.error(e.getLocalizedMessage(), e);
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e.getLocalizedMessage(), e);
      }
   }

}
