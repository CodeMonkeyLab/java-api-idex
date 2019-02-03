package com.cml.idex.packets;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.cml.idex.value.Volume;
import com.cml.idex.value.VolumePair;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

public class Return24Volume implements Req, Parser<Volume> {

   private Return24Volume() {
   }

   @Override
   public String getEndpoint() {
      return "return24Volume";
   }

   @Override
   public String getPayload() {
      return "";
   }

   public static Return24Volume create() {
      return INSTANCE;
   }

   public static Return24Volume INSTANCE = new Return24Volume();

   @Override
   public Volume parse(ObjectMapper mapper, String body) {
      return fromJson(mapper, body);
   }

   public static Volume fromJson(final ObjectMapper mapper, final String body) {
      try {
         JsonNode root = mapper.readTree(body);

         final Map<String, VolumePair> volumePairs = new HashMap<>();
         final Map<String, BigDecimal> totalVolumes = new HashMap<>();

         final Iterator<Entry<String, JsonNode>> fieldItr = root.fields();
         while (fieldItr.hasNext()) {

            final Entry<String, JsonNode> node = fieldItr.next();

            if (node.getValue().getNodeType() == JsonNodeType.STRING) {
               totalVolumes.put(node.getKey(), Utils.toBD(node.getValue().asText()));
            } else {
               final Iterator<Entry<String, JsonNode>> pairItr = node.getValue().fields();
               final Entry<String, JsonNode> entry1 = pairItr.next();
               final Entry<String, JsonNode> entry2 = pairItr.next();

               if (pairItr.hasNext())
                  throw new IllegalStateException(
                        "Should only have two pairs! " + node.getKey() + " : " + node.getValue());

               volumePairs.put(node.getKey(), new VolumePair(entry1.getKey(), Utils.toBD(entry1.getValue().asText()),
                     entry2.getKey(), Utils.toBD(entry2.getValue().asText())));
            }
         }

         return new Volume(volumePairs, totalVolumes);
      } catch (IDexException e2) {
         throw e2;
      } catch (Exception e1) {
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e1.getLocalizedMessage(), e1);
      }
   }
}
