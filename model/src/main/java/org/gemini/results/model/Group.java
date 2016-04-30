/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(namespace = ModelUtils.NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class Group {

    @XmlAttribute(required = true)
    private String id;

    @XmlAttribute(required = true)
    private String eventId;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private short minNumber;

    @XmlAttribute
    private short maxNumber;

    @XmlAttribute
    private Long offset;

    protected Group() {
    }

    public Group(final String id, final String eventId,
            final String name, 
            final short minNumber, final short maxNumber,
            final Long offset) {
        this.id = id;
        this.eventId = eventId;
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

    public void setEventId(final String eventId) {
        this.eventId = eventId;
    }

    public String getEventId() {
        return this.eventId;
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

    public void setOffset(final Long offset) {
        this.offset = offset;
    }

    public Long getOffset() {
        return this.offset;
    }
}
