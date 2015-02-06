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
public class NameList extends AbstractList<String> {

    @XmlElement(name = "name")
    private List<String> names = new ArrayList<>();

    public NameList() {
    }

    public NameList(final List<String> names) {
        this.names.addAll(names);
    }

    @Override
    public String get(int index) {
        return this.names.get(index);
    }

    @Override
    public int size() {
        return this.names.size();
    }

    @Override
    public String set(int index, String element) {
        return this.names.set(index, element);
    }

    @Override
    public void add(int index, String element) {
        this.names.add(index, element);
    }

    @Override
    public String remove(int index) {
        return this.names.remove(index);
    }
}
