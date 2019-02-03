package com.cml.idex.value;

public class Outcome {

   final String  outcome;
   final Integer value;

   public Outcome(String outcome, Integer value) {
      super();
      this.outcome = outcome;
      this.value = value;
   }

   public boolean isSuccessful() {
      return value == 0;
   }
}