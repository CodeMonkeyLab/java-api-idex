package com.cml.idex.packets;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ParserWithCurser<V> {

   V parse(final ObjectMapper mapper, final String json, final String nextCursor);
}
