/*
 * Copyright (C) 2014 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.model;

import java.util.List;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlRootElement(namespace = ModelUtils.NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class Competition {

    @XmlAttribute
    private String id;

    /**
     * Official start time of the competition. This is typically the
     * starting time of the first competitor, and anyway used as a base
     * time of for the start times. Start times of individual competitors
     * are represents as offsets to this time. This makes the system
     * tolerant for late delays. 
     */
    @XmlAttribute
    private XMLGregorianCalendar time;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String organizer;

    @XmlElement(name = "startGroup")
    private StartGroupList startGroups = new StartGroupList();

    protected Competition() {
    }

    public Competition(final String id,
            final XMLGregorianCalendar time,
            final String name, final String organizer,
            final StartGroupList startGroups) {
        this.id = id == null ? UUID.randomUUID().toString() : id;
        this.time = time == null ? null 
                : ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                    time.toXMLFormat());
        this.name = name;
        this.organizer = organizer;
        this.startGroups = startGroups == null
                ? new StartGroupList() : startGroups;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setTime(final XMLGregorianCalendar time) {
        this.time = ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                time.toXMLFormat());
    }

    public XMLGregorianCalendar getTime() {
        return this.time;
    }

    public void setOrganizer(final String organizer) {
        this.organizer = organizer;
    }

    public String getOrganizer() {
        return this.organizer;
    }

    public List<StartGroup> getStartGroups() {
        return startGroups;
    }
}
