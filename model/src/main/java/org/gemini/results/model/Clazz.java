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
public class Clazz {

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String name;

    /** Start offset comparing to competition's time.*/
    @XmlAttribute
    private Duration offset;

    @XmlAttribute
    private String groupId;

    protected Clazz() {
    }

    public Clazz(final String id, final String name, final Duration offset,
            final String groupId) {
        this.id = id;
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

    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setOffset(final Duration offset) {
        this.offset = offset;
    }

    public Duration getOffset() {
        return this.offset;
    }

    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return this.groupId;
    }
}
