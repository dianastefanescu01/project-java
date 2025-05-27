package org.example.model;

import java.io.Serializable;
import java.util.Objects;

public class Race extends Entity<Integer> implements Serializable {
    private int distance; //50, 200, 800, 1500
    private String style; //freestyle, backstroke, butterfly, mixed
    private Integer nrOfParticipants;

    public Race(int distance, String style, Integer nrOfParticipants) {
        this.distance = distance;
        this.style = style;
        this.nrOfParticipants = nrOfParticipants;
    }

    public Race(){}

    public int getDistance() {
        return this.distance;
    }

    public void setDistance(int dist) {
        this.distance = dist;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Race)) return false;
        Race c = (Race) o;
        return this.getId() == c.getId();
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Integer getNrOfParticipants() {
        return nrOfParticipants;
    }

    public void setNrOfParticipants(Integer nrOfParticipants) {
        this.nrOfParticipants = nrOfParticipants;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    @Override
    public String toString() {
        return distance + ',' + style + ',' + nrOfParticipants;
    }
}
