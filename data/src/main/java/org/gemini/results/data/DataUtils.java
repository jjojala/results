/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.data;

import java.sql.SQLIntegrityConstraintViolationException;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

public final class DataUtils {

    public static void close(final EntityManager em) {
        if (em != null && em.isOpen()) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();

            em.close();
        }
    }

    public static <T> T find(final EntityManager em, 
            final Class<T> entityType, final Object primaryKey)
            throws PersistenceException {
        return em.find(entityType, primaryKey);
    }

    public static <T> T merge(final EntityManager em, final T entity)
            throws PersistenceException {
        return em.merge(entity);
    }

    public static void persist(final EntityManager em, final Object entity)
            throws PersistenceException {
        try {
            em.persist(entity); 
            em.flush();
        }

        catch (final EntityExistsException ex) {
            throw ex;
        }

        catch (final PersistenceException ex) {
            Throwable cause = ex.getCause();
            while (cause != null) {
                if (cause instanceof SQLIntegrityConstraintViolationException)
                    throw new EntityExistsException(ex.getMessage(), ex);

                cause = cause.getCause();
            }

            throw ex;
        }
    }

    private DataUtils() { throw new AssertionError(); }
}
