package com.cml.idex.ws;

public abstract class Header {

   final String sid;
   final String rid;

   public Header(String sid, String rid) {
      super();
      this.sid = sid;
      this.rid = rid;
   }

   public String getSid() {
      return sid;
   }

   public String getRid() {
      return rid;
   }

}
