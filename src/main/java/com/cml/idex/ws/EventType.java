package com.cml.idex.ws;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.cml.idex.ws.Category.Accounts;
import com.cml.idex.ws.Category.Chains;
import com.cml.idex.ws.Category.Markets;

/**
 * All event Types Supported by IDEX.
 *
 * @author codemonkeylab
 *
 * @param <T>
 *           Event Type Category
 */
public class EventType<T extends Category> {

   final String eventType;
   final T      cat;

   private EventType(String eventType, T cat) {
      this.eventType = eventType;
      this.cat = cat;
      final Category categories;
      if (eventType.startsWith("account_")) {
         categories = Category.SUBSCRIBE_TO_ACCOUNTS;
      } else if (eventType.startsWith("market_")) {
         categories = Category.SUBSCRIBE_TO_MARKETS;
      } else if (eventType.startsWith("chain_")) {
         categories = Category.SUBSCRIBE_TO_CHAINS;
      } else {
         throw new IllegalStateException("Unsupported event Type! " + eventType);
      }
      if (categories != cat)
         throw new IllegalStateException("Please check eventType and Catetory! " + eventType + " " + categories);
   }

   /**
    * Returns the Event Type
    *
    * @return
    */
   public String getEventType() {
      return eventType;
   }

   /**
    * Returns the Category Class for the Event Type
    *
    * @return Category Class
    */
   public T getCategoryType() {
      return cat;
   }

   /**
    * Returns the Category Name
    *
    * @return Category Name
    */
   public String getCategory() {
      return cat.getName();
   }

   /**
    * Returns the EventType for the provided event Type Name.
    *
    * @param eventTypeName
    *           Event Type Name.
    * @return EventType
    */
   public static EventType<?> getEventType(final String eventTypeName) {
      return EVENTS_MAP.get(eventTypeName);
   }

   public static final EventType<Accounts>                        ACCOUNT_NONCE                   = new EventType<>(
         "account_nonce", Category.SUBSCRIBE_TO_ACCOUNTS);
   public static final EventType<Accounts>                        ACCOUNT_DEPOSIT_COMPLETE        = new EventType<>(
         "account_deposit_complete", Category.SUBSCRIBE_TO_ACCOUNTS);
   public static final EventType<Accounts>                        ACCOUNT_ORDERS                  = new EventType<>(
         "account_orders", Category.SUBSCRIBE_TO_ACCOUNTS);
   public static final EventType<Accounts>                        ACCOUNT_CANCELS                 = new EventType<>(
         "account_cancels", Category.SUBSCRIBE_TO_ACCOUNTS);
   public static final EventType<Accounts>                        ACCOUNT_TRADES                  = new EventType<>(
         "account_trades", Category.SUBSCRIBE_TO_ACCOUNTS);
   public static final EventType<Accounts>                        ACCOUNT_WITHDRAWAL_CREATED      = new EventType<>(
         "account_withdrawal_created", Category.SUBSCRIBE_TO_ACCOUNTS);
   public static final EventType<Accounts>                        ACCOUNT_WITHDRAWAL_DISPATCHED   = new EventType<>(
         "account_withdrawal_dispatched", Category.SUBSCRIBE_TO_ACCOUNTS);
   public static final EventType<Accounts>                        ACCOUNT_WITHDRAWAL_COMPLETE     = new EventType<>(
         "account_withdrawal_complete", Category.SUBSCRIBE_TO_ACCOUNTS);
   public static final EventType<Accounts>                        ACCOUNT_TRADE_DISPATCHED        = new EventType<>(
         "account_trade_dispatched", Category.SUBSCRIBE_TO_ACCOUNTS);
   public static final EventType<Accounts>                        ACCOUNT_TRADE_COMPLETE          = new EventType<>(
         "account_trade_complete", Category.SUBSCRIBE_TO_ACCOUNTS);
   public static final EventType<Accounts>                        ACCOUNT_INVALIDATION_DISPATCHED = new EventType<>(
         "account_invalidation_dispatched", Category.SUBSCRIBE_TO_ACCOUNTS);
   public static final EventType<Accounts>                        ACCOUNT_INVALIDATION_COMPLETE   = new EventType<>(
         "account_invalidation_complete", Category.SUBSCRIBE_TO_ACCOUNTS);
   public static final EventType<Accounts>                        ACCOUNT_BALANCE_SHEET           = new EventType<>(
         "account_balance_sheet", Category.SUBSCRIBE_TO_ACCOUNTS);
   public static final EventType<Accounts>                        ACCOUNT_REWARDS                 = new EventType<>(
         "account_rewards", Category.SUBSCRIBE_TO_ACCOUNTS);

   public static final EventType<Markets>                         MARKET_ORDERS                   = new EventType<>(
         "market_orders", Category.SUBSCRIBE_TO_MARKETS);
   public static final EventType<Markets>                         MARKET_CANCELS                  = new EventType<>(
         "market_cancels", Category.SUBSCRIBE_TO_MARKETS);
   public static final EventType<Markets>                         MARKET_TRADES                   = new EventType<>(
         "market_trades", Category.SUBSCRIBE_TO_MARKETS);
   public static final EventType<Markets>                         MARKET_LISTING                  = new EventType<>(
         "market_listing", Category.SUBSCRIBE_TO_MARKETS);

   public static final EventType<Chains>                          CHAIN_STATUS                    = new EventType<>(
         "chain_status", Category.SUBSCRIBE_TO_CHAINS);
   public static final EventType<Chains>                          CHAIN_MARKET_LISTING            = new EventType<>(
         "chain_market_listing", Category.SUBSCRIBE_TO_CHAINS);
   public static final EventType<Chains>                          CHAIN_SERVER_BLOCK              = new EventType<>(
         "chain_server_block", Category.SUBSCRIBE_TO_CHAINS);
   public static final EventType<Chains>                          CHAIN_SYMBOL_USD_PRICE          = new EventType<>(
         "chain_symbol_usd_price", Category.SUBSCRIBE_TO_CHAINS);
   public static final EventType<Chains>                          CHAIN_REWARD_POOL_SIZE          = new EventType<>(
         "chain_reward_pool_size", Category.SUBSCRIBE_TO_CHAINS);
   public static final EventType<Chains>                          CHAIN_GAS_PRICE                 = new EventType<>(
         "chain_gas_price", Category.SUBSCRIBE_TO_CHAINS);
   public static final EventType<Chains>                          CHAIN_24HR_USD_VOLUME           = new EventType<>(
         "chain_24hr_usd_volume", Category.SUBSCRIBE_TO_CHAINS);

   public static final Map<String, EventType<? extends Category>> EVENTS_MAP                      = Arrays
         .asList(ACCOUNT_NONCE, ACCOUNT_DEPOSIT_COMPLETE, ACCOUNT_ORDERS, ACCOUNT_CANCELS, ACCOUNT_TRADES,
               ACCOUNT_WITHDRAWAL_CREATED, ACCOUNT_WITHDRAWAL_DISPATCHED, ACCOUNT_WITHDRAWAL_COMPLETE,
               ACCOUNT_TRADE_DISPATCHED, ACCOUNT_TRADE_COMPLETE, ACCOUNT_INVALIDATION_DISPATCHED,
               ACCOUNT_INVALIDATION_COMPLETE, ACCOUNT_BALANCE_SHEET, ACCOUNT_REWARDS, MARKET_ORDERS, MARKET_CANCELS,
               MARKET_TRADES, MARKET_LISTING, CHAIN_STATUS, CHAIN_MARKET_LISTING, CHAIN_SERVER_BLOCK,
               CHAIN_SYMBOL_USD_PRICE, CHAIN_REWARD_POOL_SIZE, CHAIN_GAS_PRICE, CHAIN_24HR_USD_VOLUME)
         .stream().collect(Collectors.toMap(EventType::getEventType, val -> val));
}
