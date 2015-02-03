/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.model;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

public abstract class ModelUtils {

    public static final String NAMESPACE =
            "urn:org:gemini:results:model";

    public static JAXBContext getJaxbContext() {
        return JaxbContextHolder.getJaxbContext();
    }

    public static DatatypeFactory getDatatypeFactory() {
        return DatatypeFactoryHolder.getDatatypeFactory();
    }
    private ModelUtils() { throw new AssertionError(); }

    private static class JaxbContextHolder {
        private final static JAXBContext CONTEXT;

        public static JAXBContext getJaxbContext() {
            return CONTEXT;
        }

        static {
            try {
                CONTEXT = JAXBContext.newInstance(
                        Competition.class, Competitor.class,
                        CompetitorList.class);
            }

            catch (final JAXBException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static class DatatypeFactoryHolder {
        private final static DatatypeFactory FACTORY;

        public static DatatypeFactory getDatatypeFactory() {
            return FACTORY;
        }

        static {
            try { FACTORY = DatatypeFactory.newInstance(); }
            catch (final DatatypeConfigurationException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
