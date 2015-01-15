/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.model;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(namespace = ModelUtils.NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class StartGroupList extends AbstractList<StartGroup> {

    @XmlElement(name = "startGroup")
    private List<StartGroup> competitors = new ArrayList<>();

    protected StartGroupList() {
    }

    public StartGroupList(final List<StartGroup> items) {
        this.competitors.addAll(items);
    }

    @Override
    public StartGroup get(int index) {
        return this.competitors.get(index);
    }

    @Override
    public int size() {
        return this.competitors.size();
    }

    @Override
    public StartGroup set(int index, StartGroup element) {
        return this.competitors.set(index, element);
    }

    @Override
    public void add(int index, StartGroup element) {
        this.competitors.add(index, element);
    }

    @Override
    public StartGroup remove(int index) {
        return this.competitors.remove(index);
    }
}
