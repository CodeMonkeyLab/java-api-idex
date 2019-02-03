module com.cml.idex.api {
   requires com.fasterxml.jackson.core;
   requires java.net.http;
   requires com.fasterxml.jackson.databind;
   requires crypto;
   requires utils;

   opens com.cml.idex.packets to com.fasterxml.jackson.databind;
}