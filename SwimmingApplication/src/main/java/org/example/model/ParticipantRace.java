package org.example.model;

public class ParticipantRace extends Entity<Integer> {
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

    @Override
    public String toString() {
        return getParticipantId().getName() + ',' + getRaceId().getStyle() + ',' + getRaceId().getDistance();
    }
}
