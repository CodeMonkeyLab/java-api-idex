package com.cml.idex.ws.packets;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.cml.idex.util.Utils;
import com.cml.idex.ws.Category;
import com.cml.idex.ws.EventType;
import com.fasterxml.jackson.databind.JsonNode;

public class ActionPayload<T extends Category> {

   final Action            action;
   final Set<String>       topics;
   final Set<EventType<T>> events;

   public ActionPayload(Action action, Set<String> topics, Set<EventType<T>> events) {
      super();
      Objects.requireNonNull(topics, "topics is required");
      if (topics.isEmpty())
         throw new IllegalArgumentException("Require at least 1 topic");

      this.action = action == null ? Action.subscribe : action;
      this.topics = topics;
      this.events = events;

      if (action == Action.subscribe) {
         Objects.requireNonNull(events, "Events is required");
         if (events.isEmpty())
            throw new IllegalArgumentException("Require at least 1 event");
      }
   }

   public Action getAction() {
      return action;
   }

   public Set<String> getTopics() {
      return topics;
   }

   public Set<EventType<T>> getEvents() {
      return events;
   }

   @Override
   public String toString() {
      return "ActionPayload [action=" + action + ", topics=" + topics + ", events=" + events + "]";
   }

   public String getPayload() {
      StringBuilder sb = new StringBuilder("{\\\"action\\\": \\\"").append(action.name())
            .append("\\\", \\\"topics\\\": [");
      sb.append(topics.stream().map(tp -> "\\\"" + tp).collect(Collectors.joining("\\\",")));
      sb.append("\\\"], \\\"events\\\": [");
      sb.append(
            events.stream().map(EventType::getEventType).map(ev -> "\\\"" + ev).collect(Collectors.joining("\\\",")));
      return sb.append("\\\"]}").toString();
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   public static <T extends Category> ActionPayload<T> parse(final JsonNode node) {
      final String actiontxt = node.get("action").asText();
      Action action = Action.fromString(actiontxt);

      final Iterator<JsonNode> topicsItr = node.get("topics").elements();
      Set<String> topics = Utils.iteratorToStream(topicsItr).map(JsonNode::asText).collect(Collectors.toSet());

      final Iterator<JsonNode> eventsItr = node.get("events").elements();
      Set<String> events = Utils.iteratorToStream(eventsItr).map(JsonNode::asText).collect(Collectors.toSet());
      return new ActionPayload<T>(action, topics,
            (Set) events.stream().map(et -> EventType.EVENTS_MAP.get(et)).collect(Collectors.toSet()));
   }

   public static enum Action {
      subscribe,
      get,
      unsubscribe,
      clear;

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
