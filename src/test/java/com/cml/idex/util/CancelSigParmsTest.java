package com.cml.idex.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.bouncycastle.util.Arrays;
import org.junit.jupiter.api.Test;
import org.web3j.utils.Numeric;

import com.cml.idex.sig.CancelSigParms;

class CancelSigParmsTest {

   @Test
   void testEncode() throws IOException {
      final CancelSigParms sig =
            new CancelSigParms("0xfdfc1f34aff580e6fb96e918974ace86ea3a396498424200fa5e806a2623ac68", 7L);

      final byte[] encodedParms = sig.encode();

      final byte[] expected = Numeric.hexStringToByteArray(
            "0xfdfc1f34aff580e6fb96e918974ace86ea3a396498424200fa5e806a2623ac680000000000000000000000000000000000000000000000000000000000000007");

      assertTrue(Arrays.areEqual(encodedParms, expected));
   }

}
