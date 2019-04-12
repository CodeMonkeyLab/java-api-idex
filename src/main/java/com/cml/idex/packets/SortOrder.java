package com.cml.idex.packets;

public enum SortOrder {

   ASC("asc"),
   DESC("desc");

   final String value;

   private SortOrder(String value) {
      this.value = value;
   }

   public String getValue() {
      return value;
   }

}
