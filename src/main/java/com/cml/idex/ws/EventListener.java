package com.cml.idex.ws;

import com.cml.idex.ws.event.Event;

@FunctionalInterface
public interface EventListener<T extends Event<?>> {

   public void onEvent(T event);

}
