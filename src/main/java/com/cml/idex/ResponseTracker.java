package com.cml.idex;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.cml.idex.ws.Category;
import com.cml.idex.ws.packets.Response;

class ResponseTracker {

   private final List<CompletableFutureTimed<?>> list = new LinkedList<>();

   ResponseTracker() {
      super();
   }

   public synchronized <T extends Category> CompletableFuture<Response<T>> registerRid(final String rid) {
      CompletableFutureTimed<T> cfTimed = new CompletableFutureTimed<>(rid);
      list.add(cfTimed);
      return cfTimed;
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   synchronized <T extends Category> CompletableFuture<Response<T>> getAndRemoveFutureForRid(final String rid) {
      Iterator<CompletableFutureTimed<?>> itr = list.iterator();
      if (itr.hasNext()) {
         CompletableFutureTimed<?> val = itr.next();
         if (rid.equals(val.getRid())) {
            try {
               return (CompletableFuture) val;
            } finally {
               itr.remove();
            }
         } else if (val.isExpired()) {
            itr.remove();
         }
      }
      return null;
   }

   static final class CompletableFutureTimed<T extends Category> extends CompletableFuture<Response<T>> {
      final String rid;
      final long   time;

      public CompletableFutureTimed(String rid) {
         super();
         this.time = System.currentTimeMillis();
         this.rid = rid;
      }

      public String getRid() {
         return rid;
      }

      public boolean isExpired() {
         return (System.currentTimeMillis() - time) > 5000L;
      }
   }
}
