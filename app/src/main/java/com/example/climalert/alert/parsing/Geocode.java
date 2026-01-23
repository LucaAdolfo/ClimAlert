package com.example.climalert.alert.parsing;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Xml;
/*
     <valueName>EMMA_ID</valueName>
      <value>IT019</value>
*/
@Xml(name="cap:geocode")
public class Geocode {
    @Element(name = "valueName")
    String valueName;

    public String getValue() {
        return value;
    }

    public String getValueName() {
        return valueName;
    }

    @Element(name = "value")
    String value;

}
