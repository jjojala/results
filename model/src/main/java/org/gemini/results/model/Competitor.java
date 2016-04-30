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
public class Competitor {

    @XmlAttribute(required = true)
    private String id;

    @XmlAttribute(required = true)
    private String eventId;

    @XmlAttribute(required = true)
    private String name;

    @XmlAttribute
    private String clazzId;

    @XmlAttribute
    private short number;

    @XmlAttribute
    private Long offset;

    @XmlAttribute
    private Long finish;

    protected Competitor() {
    }

    public Competitor(final String id, final String eventId,
            final String name, final String clazzId, final short number,
            final Long offset, final Long finish) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.clazzId = clazzId;
        this.number = number;
        this.offset = offset;
        this.finish = finish;
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

    public void setClazzId(final String clazzId) {
        this.clazzId = clazzId;
    }

    public String getClazzId() {
        return this.clazzId;
    }

    public void setNumber(final short number) {
        this.number = number;
    }

    public short getNumber() {
        return this.number;
    }

    public void setOffset(final Long offset) {
        this.offset = offset;
    }

    public Long getOffset() {
        return this.offset;
    }

    public void setFinish(final Long finish) {
        this.finish = finish;
    }

    public Long getFinish() {
        return this.finish;
    }
}
