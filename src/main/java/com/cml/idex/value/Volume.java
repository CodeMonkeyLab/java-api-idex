package com.cml.idex.value;

import java.math.BigDecimal;
import java.util.Map;

public class Volume {

   private final Map<String, VolumePair> volumePairs;
   private final Map<String, BigDecimal> totalVolumes;

   public Volume(Map<String, VolumePair> volumePairs, Map<String, BigDecimal> totalVolumes) {
      super();
      this.volumePairs = volumePairs;
      this.totalVolumes = totalVolumes;
   }

   public Map<String, VolumePair> getVolumePairs() {
      return volumePairs;
   }

   public Map<String, BigDecimal> getTotalVolumes() {
      return totalVolumes;
   }

}
