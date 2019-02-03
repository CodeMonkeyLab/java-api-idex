package com.cml.idex.packets;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface Parser<V> {

   V parse(final ObjectMapper mapper, final String body);
}
