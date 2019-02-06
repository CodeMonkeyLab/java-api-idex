package com.cml.idex.value;

import java.util.List;

/**
 * Deposit and Withdrawal history
 * 
 * @author plouw
 *
 */
public class DepositsWithdrawals {

   final List<DepositHistory>  deposits;
   final List<WithdrawHistory> withdrawals;

   public DepositsWithdrawals(List<DepositHistory> deposits, List<WithdrawHistory> withdrawals) {
      super();
      this.deposits = deposits;
      this.withdrawals = withdrawals;
   }

   /**
    * List of Historical Deposits.
    * 
    * @return List of DepositHistory
    */
   public List<DepositHistory> getDeposits() {
      return deposits;
   }

   /**
    * List of Historical Withdrawals.
    * 
    * @return List of WithdrawHistory
    */
   public List<WithdrawHistory> getWithdrawals() {
      return withdrawals;
   }

}
