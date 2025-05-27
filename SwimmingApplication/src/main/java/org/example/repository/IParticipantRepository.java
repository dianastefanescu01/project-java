package org.example.repository;

import org.example.model.Participant;

import java.util.ArrayList;


public interface IParticipantRepository extends IRepository<Integer,Participant>{
    ArrayList<Participant> findByName(String name);
}
