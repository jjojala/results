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
public class GroupList extends AbstractList<Group> {

    @XmlElement(name = "group")
    private List<Group> groups = new ArrayList<>();

    public GroupList() {
    }

    public GroupList(final List<Group> items) {
        this.groups.addAll(items);
    }

    @Override
    public Group get(int index) {
        return this.groups.get(index);
    }

    @Override
    public int size() {
        return this.groups.size();
    }

    @Override
    public Group set(int index, Group element) {
        return this.groups.set(index, element);
    }

    @Override
    public void add(int index, Group element) {
        this.groups.add(index, element);
    }

    @Override
    public Group remove(int index) {
        return this.groups.remove(index);
    }
}
