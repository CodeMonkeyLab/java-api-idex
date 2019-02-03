package com.cml.idex;

public class IDexException extends RuntimeException {

   private static final long serialVersionUID = -8009983154608692696L;

   private final ErrorCode   error;

   public IDexException(final ErrorCode error) {
      super();
      this.error = error;
   }

   public IDexException(
         final ErrorCode error, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace
   ) {
      super(message, cause, enableSuppression, writableStackTrace);
      this.error = error;
   }

   public IDexException(final ErrorCode error, String message, Throwable cause) {
      super(message, cause);
      this.error = error;
   }

   public IDexException(final ErrorCode error, String message) {
      super(message);
      this.error = error;
   }

   public IDexException(final ErrorCode error, Throwable cause) {
      super(cause);
      this.error = error;
   }

   public ErrorCode getErrorCode() {
      return error;
   }

   @Override
   public String getLocalizedMessage() {
      return error + " : " + super.getLocalizedMessage();
   }
}
