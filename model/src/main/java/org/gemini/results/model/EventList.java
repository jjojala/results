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
public class EventList extends AbstractList<Event> {

    @XmlElement(name = "competition")
    private List<Event> competitions = new ArrayList<>();

    public EventList() {
    }

    public EventList(final List<Event> items) {
        this.competitions.addAll(items);
    }

    @Override
    public Event get(int index) {
        return this.competitions.get(index);
    }

    @Override
    public int size() {
        return this.competitions.size();
    }

    @Override
    public Event set(int index, Event element) {
        return this.competitions.set(index, element);
    }

    @Override
    public void add(int index, Event element) {
        this.competitions.add(index, element);
    }

    @Override
    public Event remove(int index) {
        return this.competitions.remove(index);
    }
}
