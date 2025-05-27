package org.example.service;

import org.example.model.ParticipantRace;
import org.example.repository.IParticipantRaceRepository;

import java.util.List;
import java.util.stream.StreamSupport;

public class ParticipantRaceService {
    IParticipantRaceRepository repository;

    public ParticipantRaceService(IParticipantRaceRepository repo){
        this.repository = repo;
    }

    public void addPr(ParticipantRace pr){
        this.repository.save(pr);
    }

    public void updatePr(ParticipantRace pr){
        this.repository.update(pr);
    }

    public void deletePr(Integer id){
        this.repository.delete(id);
    }

    public ParticipantRace findPr(Integer id){
        return repository.find(id).isPresent() ? repository.find(id).get() : null;
    }

    public List<ParticipantRace> findAllPr(){
        return StreamSupport.stream(repository.findAll().spliterator(), false).toList();
    }
}
