package com.cml.idex.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {

   private Utils() {
   }

   public static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

   /**
    * Produces DateTime from given node and field name. Used by WebSockets.
    *
    * @param node
    *           JsonNode
    * @param fieldName
    *           Field Name
    * @return LocalDateTime
    */
   public static final ZonedDateTime parseDateWs(final JsonNode node, final String fieldName) {
      return ZonedDateTime.parse(node.get(fieldName).asText());
   }

   /**
    * Produces DateTime from given node and field name
    *
    * @param node
    *           JsonNode
    * @param fieldName
    *           Field Name
    * @return LocalDateTime
    */
   public static final LocalDateTime parseDate(final JsonNode node, final String fieldName) {
      return LocalDateTime.parse(node.get(fieldName).asText(), DT_FORMATTER);
   }

   /**
    * Checks if the JSON is error and throws exception if error found.
    *
    * @param root
    *           JsonNode
    */
   public static final void checkError(final JsonNode root) {
      if (root.get("error") != null)
         throw new IDexException(ErrorCode.GENERIC, root.get("error").asText());
   }

   /**
    * Returns true if the json is empty.
    *
    * @param json
    *           JSON string to check
    * @return true if JSON string is "emptry"
    */
   public static final boolean isEmptyJson(final String json) {
      return json == null || json.length() < 3;
   }

   /**
    * Returns the BigDecimal for the fieldName. Not Required so returns null if
    * field is not found.
    *
    * @param node
    *           JSON Node
    * @param fieldName
    *           JSON field name to process
    * @return BigDecimal value for fieldName
    */
   public static final BigDecimal toBD(final JsonNode node, final String fieldName) {
      if (node.get(fieldName) == null)
         return null;
      return toBD(node.get(fieldName).asText());
   }

   /**
    * Returns the BigDecimal for the fieldName. If not found will throw a
    * exception.
    *
    * @param node
    *           JSON Node
    * @param fieldName
    *           JSON field name to process
    * @return BigDecimal value for fieldName
    */
   public static final BigDecimal toBDrequired(final JsonNode node, final String fieldName) {
      return toBD(node.get(fieldName).asText());
   }

   /**
    * Converts the String to BigDecimal.
    *
    * @param value
    *           String to convert
    * @return BigDecimal value
    */
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

   public static Long toEpochSecond(final LocalDateTime dateTime) {
      if (dateTime == null)
         return null;
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

   public static String prettyfyJson(ObjectMapper mapper, String body) {
      try {
         Object json = mapper.readValue(body, Object.class);
         return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
      } catch (JsonProcessingException e) {
         e.printStackTrace();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return null;
   }

   public static void prettyPrint(ObjectMapper mapper, String body) {
      System.out.println(prettyfyJson(mapper, body));
   }

   /**
    * Converters a Iterator to a Stream.
    *
    * @param <T>
    *           Stream and Iterator type.
    * @param itr
    *           Iterator
    * @return Stream
    */
   public static <T> Stream<T> iteratorToStream(Iterator<T> itr) {
      return StreamSupport.stream(Spliterators.spliteratorUnknownSize(itr, Spliterator.ORDERED), false);
   }
}
