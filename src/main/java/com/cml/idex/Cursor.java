package com.cml.idex;

class Cursor {

   String nextCursor;

   Cursor(String nextCursor) {
      super();
      this.nextCursor = nextCursor;
   }

   String getNextCursor() {
      return nextCursor;
   }

   void setNextCursor(String nextCursor) {
      if (nextCursor != null)
         this.nextCursor = nextCursor;
   }

}
