package com.cml.idex.value;

public enum WithdrawStatus {
   PENDING,
   PROCESSING,
   COMPLETE;

   public static final WithdrawStatus fromString(final String value) {
      if (value == null || value.length() < 7)
         return null;

      for (WithdrawStatus status : values()) {
         if (value.contains(status.name()))
            return status;
      }
      return null;
   }
}
