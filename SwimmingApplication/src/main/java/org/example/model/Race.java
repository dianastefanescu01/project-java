package org.example.model;

public class Race extends Entity<Integer> {
    private int distance; //50, 200, 800, 1500
    private String style; //freestyle, backstroke, butterfly, mixed
    private int nrOfParticipants;

    public Race(int distance, String style, int nrOfParticipants) {
        this.distance = distance;
        this.style = style;
        this.nrOfParticipants = nrOfParticipants;
    }

    public int getDistance() {
        return this.distance;
    }

    public void setDistance(int dist) {
        this.distance = dist;

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

    public String toString() {
        return getID().toString() + ',' + distance + ',' + style.toString() + ',' + nrOfParticipants;
    }
}
