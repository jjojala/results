/*
 * Copyright (C) 2014 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.model;

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
    private StartGroupList startGroups;

    protected Competition() {
    }

    public Competition(final XMLGregorianCalendar time,
            final String name, final String organizer,
            final StartGroupList startGroups) {
        this.time = time;
        this.name = name;
        this.organizer = organizer;
        this.startGroups = startGroups;
    }
}
