package org.example.model.hibernate;

import jakarta.persistence.*;

@Entity
@Table(name = "ParticipantRaces")
@AttributeOverride(name = "id", column = @Column(name = "pr_id"))
public class ParticipantRaceHibernate extends EntityHibernate<Integer>{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private ParticipantHibernate participantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id", nullable = false)
    private RaceHibernate raceId;

    private Integer id;

    public ParticipantRaceHibernate() {
    }

    public ParticipantRaceHibernate(Integer id, Integer participantId, Integer raceId) {
        this.id = id;
        this.participantId = new ParticipantHibernate();
        this.participantId.setId(participantId);
        this.raceId = new RaceHibernate();
        this.raceId.setId(raceId);
    }

    public ParticipantRaceHibernate(ParticipantHibernate participantId, RaceHibernate raceId) {
        this.participantId = participantId;
        this.raceId = raceId;
    }

    public ParticipantHibernate getParticipantId() {
        return participantId;
    }

    public void setParticipantId(ParticipantHibernate participantId) {
        this.participantId = participantId;
    }

    public RaceHibernate getRaceId() {
        return raceId;
    }

    public void setRaceId(RaceHibernate raceId) {
        this.raceId = raceId;
    }

    @Transient
    public String getRaceStyle() {
        return raceId != null ? raceId.getStyle() : null;
    }

    @Transient
    public String getParticipantName() {
        return participantId != null ? participantId.getName() : null;
    }

    public ParticipantHibernate getParticipant(){
        return participantId;
    }

    public RaceHibernate getRace() {
        return raceId;
    }

    @Override
    public String toString() {
        return getParticipantName() + ',' + getRaceStyle() + ',' +
                (raceId != null ? raceId.getDistance() : "N/A");
    }
}
