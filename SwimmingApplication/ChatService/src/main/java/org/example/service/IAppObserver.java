package org.example.service;

import org.example.model.Participant;
import org.example.model.ParticipantRace;
import org.example.model.User;

public interface IAppObserver {
    void userLoggedIn(User user) throws ApplicationException;
    void userLoggedOut(User user) throws ApplicationException;
    void registrationMade(ParticipantRace registration) throws ApplicationException;
    void addParticipant(Participant participant) throws ApplicationException;
}
