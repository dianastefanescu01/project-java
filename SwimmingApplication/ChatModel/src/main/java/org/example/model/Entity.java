package org.example.model;

import java.io.Serializable;
import java.util.Objects;

public class Entity<ID> implements Serializable {
    private ID id;

    public ID getId(){
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
        Entity<?> entity = ((Entity<?>) obj);
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
