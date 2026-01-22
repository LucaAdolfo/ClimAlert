package com.example.climalert.alert.parsing;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Xml;

@Xml
public class Entry {

    @Element(name = "link")
    private LinkXML link;


}
