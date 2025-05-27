package org.example.server;

import org.example.model.Race;
import org.example.repository.IRaceRepository;

import java.util.List;
import java.util.stream.StreamSupport;

public class RaceService {
    private IRaceRepository raceRepository;

    public RaceService(IRaceRepository repository){
        this.raceRepository = repository;
    }

    public void addRace(Race race){
        this.raceRepository.save(race);
    }

    public void updateRace(Race race){
        this.raceRepository.update(race);
    }

    public void deleteRace(Integer id){
        this.raceRepository.delete(id);
    }

    public Race findRace(Integer id){
        return raceRepository.find(id).isPresent() ? raceRepository.find(id).get() : null;
    }

    public List<Race> findAllRaces(){
        return StreamSupport.stream(raceRepository.findAll().spliterator(), false).toList();
    }
}
