/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;

@XmlRootElement(namespace = ModelUtils.NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class StartGroup {

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private short minNumber;

    @XmlAttribute
    private short maxNumber;

    @XmlAttribute
    private Duration offset;

    @XmlElement(name = "class")
    private ClazzList classes;

    protected StartGroup() {
    }

    public StartGroup(final String id, final String name, 
            final short minNumber, final short maxNumber,
            final Duration offset, final ClazzList classes) {
        this.id = id;
        this.name = name;
        this.minNumber = minNumber;
        this.maxNumber = maxNumber;
        this.offset = offset;
        this.classes = classes;
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
}
