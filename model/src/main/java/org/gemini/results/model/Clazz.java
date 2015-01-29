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
public class Clazz {

    @XmlAttribute(required = true)
    private String id;

    @XmlAttribute(required = true)
    private String competitionId;

    @XmlAttribute(required = true)
    private String name;

    /** Time offset in milliseconds for the starting time of this class. */
    @XmlAttribute
    private Long offset;

    @XmlAttribute
    private String groupId;

    protected Clazz() {
    }

    public Clazz(final String id, final String competitionId,
            final String name, final Long offset,
            final String groupId) {
        this.id = id;
        this.competitionId = competitionId;
        this.name = name;
        this.offset = offset;
        this.groupId = groupId;
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

    public void setOffset(final Long offset) {
        this.offset = offset;
    }

    public Long getOffset() {
        return this.offset;
    }

    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return this.groupId;
    }
}
