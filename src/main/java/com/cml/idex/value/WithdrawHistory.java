package com.cml.idex.value;

import java.math.BigDecimal;

public class WithdrawHistory {
   final int            depositNumber;
   final String         currency;
   final BigDecimal     amount;
   final long           timestamp;
   final String         transactionHash;
   final WithdrawStatus status;

   public WithdrawHistory(
         int depositNumber, String currency, BigDecimal amount, long timestamp, String transactionHash,
         WithdrawStatus status
   ) {
      super();
      this.depositNumber = depositNumber;
      this.currency = currency;
      this.amount = amount;
      this.timestamp = timestamp;
      this.transactionHash = transactionHash;
      this.status = status;
   }

   public int getDepositNumber() {
      return depositNumber;
   }

   public String getCurrency() {
      return currency;
   }

   public BigDecimal getAmount() {
      return amount;
   }

   public long getTimestamp() {
      return timestamp;
   }

   public String getTransactionHash() {
      return transactionHash;
   }

   public WithdrawStatus getStatus() {
      return status;
   }

   @Override
   public String toString() {
      return "Withdraw [depositNumber=" + depositNumber + ", currency=" + currency + ", amount=" + amount
            + ", timestamp=" + timestamp + ", transactionHash=" + transactionHash + ", status=" + status + "]";
   }

}
