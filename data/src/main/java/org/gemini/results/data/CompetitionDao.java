/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.data;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.gemini.results.model.Competition;

public class CompetitionDao {

    //private final EntityManager em_;
    private List<Competition> competitions_ = new ArrayList<>();

    public CompetitionDao(final EntityManager em) {
        //this.em_ = em;
    }

    public List<Competition> list() throws DataAccessException {
       return competitions_;
    }

    public Competition getCompetition(final String id)
            throws DataAccessException {
        for (final Competition c: competitions_)
            if (c.getId().equals(id))
                return c;

        return null;
    }
}
