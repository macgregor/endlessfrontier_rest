package com.macgregor.ef.dao;

import com.macgregor.ef.model.canonical.Tribe;
import org.hibernate.SessionFactory;

public class TribeDAO extends AbstractEFDAO<Tribe>  {
    public TribeDAO(SessionFactory factory) {
        super(factory);
    }
}
