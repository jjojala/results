/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;

@XmlRootElement(namespace = ModelUtils.NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class Group {

    @XmlAttribute(required = true)
    private String id;

    @XmlAttribute(required = true)
    private String competitionId;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private short minNumber;

    @XmlAttribute
    private short maxNumber;

    @XmlAttribute
    private Duration offset;

    protected Group() {
    }

    public Group(final String id, final String competitionId,
            final String name, 
            final short minNumber, final short maxNumber,
            final Duration offset) {
        this.id = id;
        this.competitionId = competitionId;
        this.name = name;
        this.minNumber = minNumber;
        this.maxNumber = maxNumber;
        this.offset = offset;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setCompetitionId(final String competitionId) {
        this.competitionId = competitionId;
    }

    public String getCompetitionId() {
        return this.competitionId;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setMinNumber(final short minNumber) {
        this.minNumber = minNumber;
    }

    public short getMinNumber() {
        return this.minNumber;
    }

    public void setMaxNumber(final short maxNumber) {
        this.maxNumber = maxNumber;
    }

    public void setOffset(final Duration offset) {
        this.offset = offset;
    }

    public Duration getOffset() {
        return this.offset;
    }
}
