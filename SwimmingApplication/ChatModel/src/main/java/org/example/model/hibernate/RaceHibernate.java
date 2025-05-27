package org.example.model.hibernate;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table (name = "Races")
@AttributeOverride(name = "id", column = @Column(name = "race_id"))
public class RaceHibernate extends EntityHibernate<Integer>{
    @Column(name = "distance", nullable = false)
    private int distance;

    @Column(name = "style", nullable = false)
    private String style;

    @Column(name = "nrOfParticipants", nullable = false)
    private int nrOfParticipants;

    public RaceHibernate() {
    }

    public RaceHibernate(int distance, String style, int nrOfParticipants) {
        this.distance = distance;
        this.style = style;
        this.nrOfParticipants = nrOfParticipants;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public int getNrOfParticipants() {
        return nrOfParticipants;
    }

    public void setNrOfParticipants(int nrOfParticipants) {
        this.nrOfParticipants = nrOfParticipants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RaceHibernate)) return false;
        RaceHibernate that = (RaceHibernate) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return distance + "," + style + "," + nrOfParticipants;
    }
}
