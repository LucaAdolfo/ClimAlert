package com.example.climalert.alert.parsing;

import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;
/*
     <valueName>EMMA_ID</valueName>
      <value>IT019</value>
*/
@Xml(name="cap:geocode")
public class Geocode {
    @PropertyElement(name = "valueName")
    String valueName;

    public String getValue() {
        return value;
    }

    public String getValueName() {
        return valueName;
    }

    @PropertyElement(name = "value")
    String value;

}
