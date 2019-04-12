package com.cml.idex;

import java.util.function.Function;

/**
 * Returns paging results and allows you to fetch next results.
 * 
 * @author codemonkeylab
 *
 * @param <T>
 */
public class Results<T> {

   final Cursor              cursor;
   final Function<String, T> valueProvider;
   boolean                   descNoResults = false;

   Results(Cursor cursor, Function<String, T> valueProvider) {
      super();
      this.cursor = cursor;
      this.valueProvider = valueProvider;
   }

   /**
    * Returns the next results set. If sort order is ASC this object can be kept
    * to retrieve future value once available. If sort order is DESC will return
    * null at end of results.
    * 
    * @return Next result
    */
   public synchronized T next() {
      if (descNoResults)
         return null;
      return valueProvider.apply(cursor.getNextCursor());
   }

   /**
    * Value to be used for cursor to receive the next slice (or page).
    * 
    * @return Next Cursor value
    */
   public synchronized String getNextCursor() {
      return cursor.getNextCursor();
   }
}
