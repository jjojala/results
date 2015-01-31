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
public class CompetitionList extends AbstractList<Competition> {

    @XmlElement(name = "competition")
    private List<Competition> competitions = new ArrayList<>();

    public CompetitionList() {
    }

    public CompetitionList(final List<Competition> items) {
        this.competitions.addAll(items);
    }

    @Override
    public Competition get(int index) {
        return this.competitions.get(index);
    }

    @Override
    public int size() {
        return this.competitions.size();
    }

    @Override
    public Competition set(int index, Competition element) {
        return this.competitions.set(index, element);
    }

    @Override
    public void add(int index, Competition element) {
        this.competitions.add(index, element);
    }

    @Override
    public Competition remove(int index) {
        return this.competitions.remove(index);
    }
}
