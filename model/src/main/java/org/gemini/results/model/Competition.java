/*
 * Copyright (C) 2014 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.model;

import java.util.ArrayList;
import java.util.List;
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

    @XmlAttribute(required = true)
    private String id;

    /**
     * Official start time of the competition. This is typically the
     * starting time of the first competitor, and anyway used as a base
     * time of for the start times. Start times of individual competitors
     * are represents as offsets to this time. This makes the system
     * tolerant for late delays. 
     */
    @XmlAttribute(required = true)
    private XMLGregorianCalendar time;

    @XmlAttribute(required = true)
    private String name;

    @XmlAttribute
    private String organizer;

    @XmlElement(name = "group")
    private List<Group> groups;

    @XmlElement(name = "class")
    private List<Clazz> clazzes;

    @XmlElement(name = "competitor")
    private List<Competitor> competitors;

    protected Competition() {
        groups = new StartGroupList();
        clazzes = new ClazzList();
        competitors = new ArrayList<>();
    }

    public Competition(final String id,
            final XMLGregorianCalendar time,
            final String name, final String organizer,
            final List<Group> startGroups,
            final List<Clazz> clazzes,
            final List<Competitor> competitors) {
        this.id = id;
        this.time = time == null ? null 
                : ModelUtils.getDatatypeFactory().newXMLGregorianCalendar(
                    time.toXMLFormat());
        this.name = name;
        this.organizer = organizer;
        this.groups = startGroups == null
                ? new StartGroupList() : startGroups;
        this.clazzes = clazzes == null ? new ClazzList() : clazzes;
        this.competitors = competitors == null
                ? new ArrayList<Competitor>() : competitors;
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

    public List<Group> getGroups() {
        return groups;
    }

    public List<Clazz> getClasses() {
        return clazzes;
    }

    public List<Competitor> getCompetitors() {
        return this.competitors;
    }
}
