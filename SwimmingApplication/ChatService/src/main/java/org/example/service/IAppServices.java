package org.example.service;

import org.example.model.Participant;
import org.example.model.ParticipantRace;
import org.example.model.Race;
import org.example.model.User;

import java.util.List;

public interface IAppServices {
    User login(User user, IAppObserver client) throws ApplicationException;
    void logout(User user) throws ApplicationException;
    Participant getParticipantById(Integer id) throws ApplicationException;
    List<Participant> getAllParticipants() throws ApplicationException;
    List<Race> getAllRaces() throws ApplicationException;
    Race getRaceById(Integer id) throws ApplicationException;
    List<ParticipantRace> getAllRegistrations() throws ApplicationException;
    Participant addParticipant(Participant participant) throws ApplicationException;
    User[] getLoggedUsers() throws ApplicationException;
    void addRegistration(ParticipantRace registration) throws ApplicationException;
}
