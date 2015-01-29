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
public class Competitor {

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String clazzId;

    @XmlAttribute
    private short number;

    @XmlAttribute
    private Duration offset;

    @XmlAttribute
    private Duration result;

    protected Competitor() {
    }

    public Competitor(final String id, final String name, final String clazzId,
            final short number, final Duration offset, final Duration result) {
        this.id = id;
        this.name = name;
        this.clazzId = clazzId;
    }

    public final void setId(final String id) {
        this.id = id;
    }

    public final String getId() {
        return this.id;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final String getName() {
        return this.name;
    }

    public final void setClassId(final String clazzId) {
        this.clazzId = clazzId;
    }

    public final String getClassId() {
        return this.clazzId;
    }

    public final void setNumber(final short number) {
        this.number = number;
    }

    public final short getNumber() {
        return this.number;
    }

    public final void setOffset(final Duration offset) {
        this.offset = offset;
    }

    public final Duration getOffset() {
        return this.offset;
    }

    public final void setResult(final Duration result) {
        this.result = result;
    }

    public final Duration getResult() {
        return this.result;
    }
}
