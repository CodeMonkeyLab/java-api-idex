package com.cml.idex.ws;

public interface Category {

   public static final class Accounts implements Category {

      private Accounts() {
      }

      @Override
      public String getName() {
         return "subscribeToAccounts";
      }
   }

   public static final class Markets implements Category {

      private Markets() {
      }

      @Override
      public String getName() {
         return "subscribeToMarkets";
      }
   }

   public static final class Chains implements Category {
      private Chains() {
      }

      @Override
      public String getName() {
         return "subscribeToChains";
      }
   }

   public String getName();

   public static final Accounts SUBSCRIBE_TO_ACCOUNTS = new Accounts();
   public static final Markets  SUBSCRIBE_TO_MARKETS  = new Markets();
   public static final Chains   SUBSCRIBE_TO_CHAINS   = new Chains();
}
