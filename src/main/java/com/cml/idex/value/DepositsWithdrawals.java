package com.cml.idex.value;

import java.util.List;

public class DepositsWithdrawals {

   List<Deposit>  deposits;
   List<Withdraw> withdrawals;

   public DepositsWithdrawals(List<Deposit> deposits, List<Withdraw> withdrawals) {
      super();
      this.deposits = deposits;
      this.withdrawals = withdrawals;
   }

   public List<Deposit> getDeposits() {
      return deposits;
   }

   public void setDeposits(List<Deposit> deposits) {
      this.deposits = deposits;
   }

   public List<Withdraw> getWithdrawals() {
      return withdrawals;
   }

   public void setWithdrawals(List<Withdraw> withdrawals) {
      this.withdrawals = withdrawals;
   }

}
