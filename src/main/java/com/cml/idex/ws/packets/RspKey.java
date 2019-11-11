package com.cml.idex.ws.packets;

public class RspKey {

   final String  rid;
   final String  request;
   final boolean eventType;

   private RspKey(String rid, String request, boolean eventType) {
      super();
      this.rid = rid;
      this.request = request;
      this.eventType = eventType;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((request == null) ? 0 : request.hashCode());
      result = prime * result + (eventType ? 1231 : 1237);
      result = prime * result + ((rid == null) ? 0 : rid.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      RspKey other = (RspKey) obj;
      if (request == null) {
         if (other.request != null)
            return false;
      } else if (!request.equals(other.request))
         return false;
      if (eventType != other.eventType)
         return false;
      if (rid == null) {
         if (other.rid != null)
            return false;
      } else if (!rid.equals(other.rid))
         return false;
      return true;
   }

   // public RspKey fromJsonNode(final JsonNode root) {
   // JsonNode node = root.get("event");
   // if (node == null)
   // }
}
