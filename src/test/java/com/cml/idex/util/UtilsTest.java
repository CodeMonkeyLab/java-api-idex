package com.cml.idex.util;

import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class UtilsTest {

	@Test
	void testToEpochSecond() {
		assertTrue(Utils.toEpochSecond(LocalDateTime.MIN) == 0L);
		assertTrue(Utils.toEpochSecond(LocalDateTime.of(2019, 11, 11, 14, 31, 36, 0)) == 1573482696L);
	}

}
