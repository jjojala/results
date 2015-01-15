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
public class ClazzList extends AbstractList<Clazz> {

    @XmlElement(name = "class")
    private List<Clazz> classes = new ArrayList<>();

    protected ClazzList() {
    }

    public ClazzList(final List<Clazz> classes) {
        this.classes.addAll(classes);
    }

    public Clazz getById(final String id) {
        for (final Clazz clazz: classes)
            if (clazz.getId().equals(id))
                return clazz;

        throw new NoSuchElementException(
                String.format("Clazz [id='%s'] not found!", id));
    }

    @Override
    public Clazz get(int index) {
        return this.classes.get(index);
    }

    @Override
    public int size() {
        return this.classes.size();
    }

    @Override
    public Clazz set(int index, Clazz element) {
        return this.classes.set(index, element);
    }

    @Override
    public void add(int index, Clazz element) {
        this.classes.add(index, element);
    }

    @Override
    public Clazz remove(int index) {
        return this.classes.remove(index);
    }
}
