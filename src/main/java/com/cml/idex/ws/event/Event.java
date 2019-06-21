package com.cml.idex.ws.event;

import com.cml.idex.ws.Category;
import com.cml.idex.ws.EventType;

public abstract class Event<T extends Category> {

   final String chain;
   final long   seqID;
   final String eventID;

   public Event(String chain, long seqID, String eventID) {
      super();
      this.chain = chain;
      this.seqID = seqID;
      this.eventID = eventID;
   }

   /**
    * Returns the chain.
    *
    * @return Chain
    */
   public String getChain() {
      return chain;
   }

   /**
    * Returns the Sequence ID.
    *
    * @return Sequence ID
    */
   public long getSeqID() {
      return seqID;
   }

   /**
    * Returns the Event ID.
    *
    * @return Event ID
    */
   public String getEventID() {
      return eventID;
   }

   /**
    * Returns the Event Type.
    *
    * @return Event Type
    */
   public abstract EventType<T> getEventType();
}
