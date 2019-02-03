package com.cml.idex.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {

   private Utils() {
   }

   public static final boolean isEmptyJson(final String json) {
      return json == null || json.length() < 3;
   }

   public static final BigDecimal toBD(final JsonNode node, final String fieldName) {
      return toBD(node.get(fieldName).asText());
   }

   public static final BigDecimal toBD(final String value) {
      try {
         return new BigDecimal(value);
      } catch (@SuppressWarnings("unused") NumberFormatException e) {
      }
      if ("N/A".equalsIgnoreCase(value))
         return null;
      throw new NumberFormatException(value);
   }

   public static boolean isValidEthAddress(byte[] addr) {
      return addr != null && addr.length == 20;
   }

   public static long toEpochSecond(final LocalDateTime dateTime) {
      final ZoneId zoneId = ZoneId.systemDefault();
      return dateTime.atZone(zoneId).toEpochSecond();
   }

   /**
    * Strips blank spaces at beginning and end of string. Also returns
    * <tt>null</tt> if string is empty.
    *
    * @param inputStr
    *           String to fix
    * @return Fixed String
    */
   public static final String fixString(String inputStr) {
      if (inputStr == null)
         return null;
      final String fixedStr = inputStr.trim();
      if (fixedStr.isEmpty())
         return null;
      return fixedStr;
   }

   public static void prettyPrint(ObjectMapper mapper, String body) {
      try {
         System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body));
      } catch (JsonProcessingException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

}
