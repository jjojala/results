/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.model;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(namespace = ModelUtils.NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class CompetitorList extends AbstractList<Competitor> {

    @XmlElement(name = "competitor")
    private List<Competitor> competitors = new ArrayList<>();

    public CompetitorList() {
    }

    public CompetitorList(final List<Competitor> items) {
        this.competitors.addAll(items);
    }

    @Override
    public Competitor get(int index) {
        return this.competitors.get(index);
    }

    @Override
    public int size() {
        return this.competitors.size();
    }

    @Override
    public Competitor set(int index, Competitor element) {
        return this.competitors.set(index, element);
    }

    @Override
    public void add(int index, Competitor element) {
        this.competitors.add(index, element);
    }

    @Override
    public Competitor remove(int index) {
        return this.competitors.remove(index);
    }
}
