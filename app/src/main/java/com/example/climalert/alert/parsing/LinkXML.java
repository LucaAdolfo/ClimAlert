package com.example.climalert.alert.parsing;

import com.tickaroo.tikxml.annotation.Attribute;
import com.tickaroo.tikxml.annotation.Xml;

@Xml(name="link")
public class LinkXML {

    @Attribute(name="title")
    String title;

    @Attribute(name="href")
    String href;

    @Attribute(name="hreflang")
    String hreflang;
}
