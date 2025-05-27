package org.example.model;

import java.io.Serializable;

public class ParticipantRace extends Entity<Integer> implements Serializable {
    private Participant participantId;
    private Race raceId;

    public ParticipantRace(Participant participantId, Race raceId) {
        this.participantId = participantId;
        this.raceId = raceId;
    }

    public Participant getParticipantId() { return participantId; }
    public void setParticipantId(Participant participantId) { this.participantId = participantId; }

    public Race getRaceId() { return raceId; }
    public void setRaceId(Race raceId) { this.raceId=raceId;}

    public String getRaceStyle(){
        return raceId.getStyle();
    }

    public String getParticipantName(){
        return participantId.getName();
    }
    @Override
    public String toString() {
        return getParticipantId().getName() + ',' + getRaceId().getStyle() + ',' + getRaceId().getDistance();
    }
}
