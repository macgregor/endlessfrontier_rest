package com.macgregor.ef.dao;

import com.macgregor.ef.model.canonical.Pet;
import org.hibernate.SessionFactory;

public class PetDAO extends AbstractEFDAO<Pet> {
    public PetDAO(SessionFactory factory) {
        super(factory);
    }
}
