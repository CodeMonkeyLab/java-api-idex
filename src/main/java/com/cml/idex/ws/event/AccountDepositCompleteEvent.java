package com.cml.idex.ws.event;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
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
 * Received when a deposit is received and credited to the account.
 * <p>
 * At this point the deposited funds are available for trading.
 * <p>
 * NOTE: Deposits are not submitted using an API Request and are instead
 * initiated directly with the IDEX Contract. This event is generated using our
 * internal block scanner which is actively monitoring transactions on the
 * network.
 * <p>
 * NOTE: Deposit does not have dispatched and pending events associated with
 * them as they are directly executed on the IDEX Client and are never processed
 * by the IDEX Backend until they are seen in the blockchain.
 *
 * @author codemonkeylab
 *
 */
public class AccountDepositCompleteEvent extends Event<Accounts> {
   private static final Logger             log             = LoggerFactory.getLogger(AccountDepositCompleteEvent.class);

   public static final EventType<Accounts> EVENT_TYPE      = EventType.ACCOUNT_DEPOSIT_COMPLETE;
   public static final String              EVENT_TYPE_NAME = "account_deposit_complete";

   final String                            account;
   final long                              id;
   final String                            user;
   final String                            token;
   final BigDecimal                        amount;
   final String                            transactionHash;
   final ZonedDateTime                     createdAt;

   public AccountDepositCompleteEvent(
         String chain, long seqID, String eventID, String account, long id, String user, String token,
         BigDecimal amount, String transactionHash, ZonedDateTime createdAt
   ) {
      super(chain, seqID, eventID);
      this.account = account;
      this.id = id;
      this.user = user;
      this.token = token;
      this.amount = amount;
      this.transactionHash = transactionHash;
      this.createdAt = createdAt;
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
    * Deposit ID.
    *
    * @return ID
    */
   public long getId() {
      return id;
   }

   public String getUser() {
      return user;
   }

   public String getToken() {
      return token;
   }

   public BigDecimal getAmount() {
      return amount;
   }

   public String getTransactionHash() {
      return transactionHash;
   }

   public ZonedDateTime getCreatedAt() {
      return createdAt;
   }

   @Override
   public EventType<Accounts> getEventType() {
      return EVENT_TYPE;
   }

   @Override
   public String toString() {
      return "AccountDepositCompleteEvent [account=" + account + ", id=" + id + ", user=" + user + ", token=" + token
            + ", amount=" + amount + ", transactionHash=" + transactionHash + ", createdAt=" + createdAt + "]";
   }

   public static AccountDepositCompleteEvent parse(final ObjectMapper mapper, final JsonNode root) {
      try {
         final String chain = Optional.of(root.get("chain")).map(JsonNode::asText).orElse(null);
         final String eid = root.get("eid").asText();
         final long seqID = root.get("seq").asLong();

         final JsonNode payload = mapper.readTree(root.get("payload").asText());
         final String account = payload.get("account").asText();
         final long id = payload.get("id").asLong();
         final String user = payload.get("user").asText();
         final String token = payload.get("token").asText();
         final BigDecimal amount = Utils.toBDrequired(payload, "amount");
         final String transactionHash = payload.get("transactionHash").asText();
         final ZonedDateTime createdAt = Utils.parseDateWs(payload, "createdAt");

         return new AccountDepositCompleteEvent(chain, seqID, eid, account, id, user, token, amount, transactionHash,
               createdAt);
      } catch (Throwable e) {
         log.error("Error parsing AccountDepositCompleteEvent!");
         log.error(Utils.prettyfyJson(mapper, root.toString()));
         log.error(e.getLocalizedMessage(), e);
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e.getLocalizedMessage(), e);
      }
   }
}
