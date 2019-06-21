package com.cml.idex;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.cml.idex.ws.Category;
import com.cml.idex.ws.packets.Response;

class ResponseTracker {

   private final List<WeakReference<CompletableFutureWithKey<?>>> list = new LinkedList<>();

   ResponseTracker() {
      super();
   }

   public synchronized <T extends Category> CompletableFuture<Response<T>> registerRid(final String rid) {
      CompletableFutureWithKey<T> completableFutureWithKey = new CompletableFutureWithKey<>(rid);
      list.add(new WeakReference<>(completableFutureWithKey));
      return completableFutureWithKey;
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   synchronized <T extends Category> CompletableFuture<Response<T>> getAndRemoveFutureForRid(final String rid) {
      Iterator<WeakReference<CompletableFutureWithKey<?>>> itr = list.iterator();
      if (itr.hasNext()) {
         WeakReference<CompletableFutureWithKey<?>> val = itr.next();
         if (val == null || val.get() == null) {
            System.out.println("Removing dead Key");
            itr.remove();
         } else if (rid.equals(val.get().getRid())) {
            try {
               return (CompletableFuture) val.get();
            } finally {
               itr.remove();
            }
         }
      }
      return null;
   }

   static final class CompletableFutureWithKey<T extends Category> extends CompletableFuture<Response<T>> {
      final String rid;

      public CompletableFutureWithKey(String rid) {
         super();
         this.rid = rid;
      }

      public String getRid() {
         return rid;
      }

   }
}
