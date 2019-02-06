package com.cml.idex.value;

import java.math.BigDecimal;

public class DepositHistory {
   final int        depositNumber;
   final String     currency;
   final BigDecimal amount;
   final long       timestamp;
   final String     transactionHash;

   public DepositHistory(int depositNumber, String currency, BigDecimal amount, long timestamp, String transactionHash) {
      super();
      this.depositNumber = depositNumber;
      this.currency = currency;
      this.amount = amount;
      this.timestamp = timestamp;
      this.transactionHash = transactionHash;
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

   @Override
   public String toString() {
      return "Deposit [depositNumber=" + depositNumber + ", currency=" + currency + ", amount=" + amount
            + ", timestamp=" + timestamp + ", transactionHash=" + transactionHash + "]";
   }
}
