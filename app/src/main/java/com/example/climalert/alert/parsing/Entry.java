package com.example.climalert.alert.parsing;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Xml;

import java.util.List;

/*
  <entry>
    <geocode>
      <valueName>EMMA_ID</valueName>
      <value>IT019</value>
    </geocode>
    <link title="Sardegna" href="https://meteoalarm.org?geocode=EMMA_ID:IT019" hreflang="en"/>
    <areaDesc>Sardegna</areaDesc>
    <event>Yellow Wind Warning</event>
    <sent>2026-01-23T11:53:40+00:00</sent>
    <expires>2026-01-25T22:59:00+00:00</expires>
    <effective>2026-01-24T06:00:00+00:00</effective>
    <onset>2026-01-24T06:00:00+00:00</onset>
    <certainty>Likely</certainty>
    <severity>Moderate</severity>
    <urgency>Future</urgency>
    <scope>Public</scope>
    <message_type>Update</message_type>
    <status>Actual</status>
    <identifier>2.49.0.0.380.3.IT.260123125339.057</identifier>
    <link type="application/cap+xml" href="https://feeds.meteoalarm.org/api/v1/warnings/feeds-italy/263cfb7c-4764-4316-b57c-b625b2ed625c"/>
    <link title="Italy" rel="related" href="https://meteoalarm.org?region=IT" hreflang="en"/>
    <author>
      <name>meteoalarm.org</name>
      <uri>https://meteoalarm.org</uri>
    </author>
    <published>2026-01-23T11:53:40Z</published>
    <id>https://feeds.meteoalarm.org/api/v1/warnings/feeds-italy/263cfb7c-4764-4316-b57c-b625b2ed625c?index_info=0&amp;index_area=0&amp;index_geocode=0</id>
    <title>Yellow Wind Warning issued for Italy - Sardegna</title>
    <updated>2026-01-23T11:53:40Z</updated>
  </entry>
* */
//Di deafult tikxml si togliere cap:!
@Xml(name="entry")
public class Entry {

    @Element(name = "link")
    List<LinkXML> link;

    @Element(name="geocode")
    Geocode geocode;

    @Element(name = "areaDesc")
    String areaDesc;

    @Element(name = "severity")
    String severity;

    @Element(name = "title")
    String title;
    @Element(name="event")
    String event;
    @Element(name="sent")
    String sent;
    @Element(name="expires")
    String expires;
    @Element(name="effective")
    String effective;
    @Element(name="onset")
    String onset;
    @Element(name="certainty")
    String certainty;
    @Element(name="urgency")
    String urgency;
    @Element(name="published")
    String published;
    @Element(name="id")
    String id;
    @Element(name="updated")
    String updated;



    public String getAreaDesc() {
        return areaDesc;
    }

    public List<LinkXML> getLink() {
        return link;
    }

    public Geocode getGeocode() {
        return geocode;
    }

    public String getSeverity() {
        return severity;
    }

    public String getTitle() {
        return title;
    }

    public String getEvent() {
        return event;
    }

    public String getSent() {
        return sent;
    }

    public String getExpires() {
        return expires;
    }

    public String getEffective() {
        return effective;
    }

    public String getOnset() {
        return onset;
    }

    public String getCertainty() {
        return certainty;
    }

    public String getUrgency() {
        return urgency;
    }

    public String getPublished() {
        return published;
    }

    public String getId() {
        return id;
    }

    public String getUpdated() {
        return updated;
    }
}
