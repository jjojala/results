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
    private String competitionId;

    @XmlAttribute(required = true)
    private String name;

    @XmlAttribute
    private String clazzId;

    @XmlAttribute
    private short number;

    @XmlAttribute
    private Long offset;

    @XmlAttribute
    private Long result;

    protected Competitor() {
    }

    public Competitor(final String id, final String competitionId,
            final String name, final String clazzId, final short number,
            final Long offset, final Long result) {
        this.id = id;
        this.competitionId = competitionId;
        this.name = name;
        this.clazzId = clazzId;
        this.number = number;
        this.offset = offset;
        this.result = result;
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

    public void setResult(final Long result) {
        this.result = result;
    }

    public Long getResult() {
        return this.result;
    }
}
