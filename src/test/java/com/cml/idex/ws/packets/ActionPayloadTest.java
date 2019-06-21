package com.cml.idex.ws.packets;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashSet;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import com.cml.idex.ws.Category;
import com.cml.idex.ws.EventType;
import com.cml.idex.ws.packets.ActionPayload.Action;

class ActionPayloadTest {

   @Test
   void testCreate() {

      ActionPayload<Category.Markets> sub = new ActionPayload<>(Action.subscribe,
            new LinkedHashSet<>(Arrays.asList("ETH_ZCC", "ETH_IDXM")),
            new LinkedHashSet<>(Arrays.asList(EventType.MARKET_LISTING, EventType.MARKET_ORDERS)));

      assertTrue(sub.getAction() == Action.subscribe);
      assertTrue(sub.getTopics() != null);
      assertTrue(sub.getTopics().size() == 2);
      assertTrue(sub.getEvents() != null);
      assertTrue(sub.getEvents().size() == 2);

      assertThat("correctEvents", sub.getEvents(), hasItems(EventType.MARKET_LISTING, EventType.MARKET_ORDERS));
      assertThat("correctTopics", sub.getTopics(), hasItems("ETH_ZCC", "ETH_IDXM"));
   }

   @Test
   void testJson01() {
      ActionPayload<Category.Markets> sub = new ActionPayload<>(Action.subscribe,
            new LinkedHashSet<>(Arrays.asList("ETH_ZCC", "ETH_IDXM")),
            new LinkedHashSet<>(Arrays.asList(EventType.MARKET_LISTING, EventType.MARKET_ORDERS)));

      assertThat(sub.getPayload(), Matchers.equalToIgnoringCase(
            "{\\\"action\\\": \\\"subscribe\\\", \\\"topics\\\": [\\\"ETH_ZCC\\\",\\\"ETH_IDXM\\\"], \\\"events\\\": [\\\"market_listing\\\",\\\"market_orders\\\"]}"));
   }

   @Test
   void testJson02() {
      ActionPayload<Category.Accounts> sub = new ActionPayload<>(Action.unsubscribe,
            new LinkedHashSet<>(Arrays.asList("ETH_ZCC", "ETH_IDXM")),
            new LinkedHashSet<>(Arrays.asList(EventType.ACCOUNT_CANCELS)));

      assertThat(sub.getPayload(), Matchers.equalToIgnoringCase(
            "{\\\"action\\\": \\\"unsubscribe\\\", \\\"topics\\\": [\\\"ETH_ZCC\\\",\\\"ETH_IDXM\\\"], \\\"events\\\": [\\\"account_cancels\\\"]}"));
   }
}
