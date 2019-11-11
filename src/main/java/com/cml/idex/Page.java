package com.cml.idex;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Returns paging results and allows you to fetch next results.
 *
 * @author codemonkeylab
 *
 * @param <T>
 *           Results
 */
public class Page<T> {

   final String                                       cursor;
   final Function<String, CompletableFuture<Page<T>>> valueProvider;
   final T                                            value;
   boolean                                            descNoResults = false;

   Page(final String cursor, Function<String, CompletableFuture<Page<T>>> valueProvider, T value) {
      super();
      this.cursor = cursor;
      this.valueProvider = valueProvider;
      this.value = value;
   }

   /**
    * Returns the current page results. Can return null.
    * 
    * @return T
    */
   public T getResults() {
      return value;
   }

   /**
    * Returns the next results set. If sort order is ASC this object can be kept
    * to retrieve future value once available. If sort order is DESC will return
    * null at end of results.
    * 
    * @return Next result
    */
   public CompletableFuture<Page<T>> nextPage() {
      if (descNoResults)
         return null;
      return valueProvider.apply(cursor);
   }

   /**
    * Value to be used for cursor to receive the next slice (or page).
    * 
    * @return Next Cursor value
    */
   public String getNextCursor() {
      return cursor;
   }
}
