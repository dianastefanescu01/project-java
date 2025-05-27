package org.example.model.hibernate;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
public abstract class EntityHibernate<ID extends Serializable> implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // or AUTO, depending on DB
    private ID id;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        EntityHibernate<?> entity = (EntityHibernate<?>) obj;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
