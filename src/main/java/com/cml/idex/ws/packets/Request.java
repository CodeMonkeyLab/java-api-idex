package com.cml.idex.ws.packets;

import java.util.Objects;
import java.util.Optional;

import com.cml.idex.ws.Category;
import com.cml.idex.ws.EventType;

public class Request<T extends Category> {

   final String           sid;
   final String           rid;
   final T                category;
   final ActionPayload<T> payload;

   public Request(final String sid, final String rid, T category, final ActionPayload<T> payload) {
      super();
      Objects.requireNonNull(sid, "sid is required");
      Objects.requireNonNull(category, "Category is required");
      this.sid = sid;
      this.rid = rid;
      this.category = category;
      this.payload = payload;

      Optional<T> badEvent = this.payload.getEvents().stream().map(EventType::getCategoryType)
            .filter(cat -> cat != category).findAny();
      if (badEvent.isPresent())
         throw new IllegalArgumentException("All events must be of same category! Found=[" + badEvent.get() + "]");
   }

   /**
    * The request category to perform.
    *
    * @return Category
    */
   public T getCategory() {
      return category;
   }

   /**
    * The Session ID which was provided as a result of the initial handshake.
    *
    * @return SessionID
    */
   public String getSid() {
      return sid;
   }

   /**
    * A unique ID that can be used to identify the given request. If not
    * provided, a unique value will be generated automatically.
    *
    * @return Request ID
    */
   public String getRid() {
      return rid;
   }

   public ActionPayload<T> getPayload() {
      return payload;
   }

   public String toJson() {
      StringBuilder sb = new StringBuilder("{\"sid\": \"").append(sid).append("\", ");
      if (rid != null)
         sb.append("\"rid\": \"").append(rid).append("\", ");

      return sb.append("\"request\": \"").append(category.getName()).append("\", ").append("\"payload\": \"")
            .append(payload.getPayload()).append("\"}").toString();
   }

}
