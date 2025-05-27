package org.example.service;

import org.example.model.Participant;
import org.example.repository.IParticipantRepository;

import java.util.List;
import java.util.stream.StreamSupport;

public class ParticipantService {
    IParticipantRepository participantRepository;

    public ParticipantService(IParticipantRepository repository){
        this.participantRepository = repository;
    }

    public void addParticipant(Participant participant){
        this.participantRepository.save(participant);
    }

    public void updateParticipant(Participant participant){
        this.participantRepository.update(participant);
    }

    public void deleteParticipant(Integer id){
        this.participantRepository.delete(id);
    }

    public Participant findParticipant(Integer id){
        return participantRepository.find(id).isPresent() ? participantRepository.find(id).get() : null;
    }

    public List<Participant> findAllParticipants(){
        return StreamSupport.stream(participantRepository.findAll().spliterator(), false).toList();
    }

    public List<Participant> findByName(String name){
        return participantRepository.findByName(name);
    }
}
