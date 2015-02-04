/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.data;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;

public final class DataUtils {

    public static void close(final EntityManager em) {
        if (em != null && em.isOpen()) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();

            em.close();
        }
    }

    public static void close(final EntityManagerFactory emf) {
        if (emf != null && emf.isOpen())
            emf.close();
    }

    public static <T> T find(final EntityManager em, 
            final Class<T> entityType, final Object primaryKey)
            throws PersistenceException {
        return em.find(entityType, primaryKey);
    }

    public static <T> T findWithLock(final EntityManager em,
            final Class<T> entityType, final Object primaryKey)
            throws PersistenceException {
        return em.find(entityType, primaryKey, LockModeType.PESSIMISTIC_READ);
    }

    public static <K,E> void create(final EntityManager em, final K primaryKey,
            final E entity) throws PersistenceException {
        if (findWithLock(em, entity.getClass(), primaryKey) != null)
            throw new EntityExistsException(
                    String.format("class=%s, primaryKey=%s",
                        entity.getClass().toString(), primaryKey.toString()));

        em.persist(entity);
    }

    public static <K,E> E update(final EntityManager em, final K primaryKey,
            final E entity) throws PersistenceException {
        if (findWithLock(em, entity.getClass(), primaryKey) == null)
            throw new EntityNotFoundException(
                    String.format("class=%s, primaryKey=%s",
                    entity.getClass().getName(), primaryKey.toString()));

        return em.merge(entity);
    }

    public static <K,T> boolean remove(final EntityManager em,
            final K primaryKey, final Class<T> entityType)
            throws PersistenceException {
        final T entity = findWithLock(em, entityType, primaryKey);
        if (entity == null)
            throw new EntityNotFoundException(makeMessage(entityType, primaryKey));

        em.remove(entity);
        return true;
    }


    @Deprecated
    public static String makeMessage(final Class<?> entityType, final Object key) {
        return String.format("class=%s, key=%s", entityType.getName(), key);
    }

    private DataUtils() { throw new AssertionError(); }
}
