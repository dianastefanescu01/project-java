package org.example.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Participant;
import org.example.model.ParticipantRace;
import org.example.model.Race;
import org.example.model.User;
import org.example.repository.*;
import org.example.service.ApplicationException;
import org.example.service.IAppObserver;
import org.example.service.IAppServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.StreamSupport;

public class AppServicesImpl implements IAppServices {
    private static final Logger logger = LogManager.getLogger(AppServicesImpl.class);

    private IRaceRepository raceRepository;
    private IUserRepository userRepository;
    private IParticipantRaceRepository registrationRepository;
    private IParticipantRepository participantRepository;
    private Map<String, IAppObserver> loggedClients;
    private final int defaultThreadsNo = 3;

    public AppServicesImpl(IRaceRepository raceRepository, IUserRepository uRepo, IParticipantRaceRepository rRepo,IParticipantRepository participantRepository) {
        this.raceRepository = raceRepository;
        this.userRepository = uRepo;
        this.registrationRepository = rRepo;
        this.participantRepository = participantRepository;
        this.loggedClients = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized User login(User user, IAppObserver client) throws ApplicationException {
        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            throw new ApplicationException("invalid credentials");
        }

        User authenticatedUser =userRepository.getUserByEmailAndPass(user.getEmail(), user.getPassword());
        if(authenticatedUser == null){
            throw new ApplicationException("Authentication failed: invalid email or password");
        }

        String email = user.getEmail();

        if(loggedClients.containsKey(email)){
            throw new ApplicationException("User already logged in");
        }

        loggedClients.put(email, client);
        logger.info("User {} logged in", email);

        return authenticatedUser;
    }

    @Override
    public synchronized void logout(User user) throws ApplicationException {
        IAppObserver removedClient = loggedClients.remove(user.getEmail());
        if (removedClient == null) {
            throw new ApplicationException("User is not logged in.");
        }
        logger.info("User {} logged out.", user.getEmail());
    }

    @Override
    public synchronized void addRegistration(ParticipantRace registration) throws ApplicationException {
        Optional<ParticipantRace> optRegistration = registrationRepository.save(registration);
        if (!optRegistration.isPresent()) {
            throw new ApplicationException("Registration not added.");
        }
        notifyUsersDonation(registration);
    }

    @Override
    public User[] getLoggedUsers() {
        return loggedClients.keySet().stream()
                .map(email -> new User(email))
                .toArray(User[]::new);
    }

    private void notifyUsersDonation(ParticipantRace registration) {
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        for (IAppObserver client : loggedClients.values()) {
            executor.execute(() -> {
                try {
                    client.registrationMade(registration);
                } catch (ApplicationException e) {
                    logger.error("Error notifying user: ", e);
                }
            });
        }
        executor.shutdown();
    }

    private void notifyUsersDonator(Participant participant) {
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        for (IAppObserver client : loggedClients.values()) {
            executor.execute(() -> {
                try {
                    client.addParticipant(participant);
                } catch (ApplicationException e) {
                    logger.error("Error notifying user: ", e);
                }
            });
        }
        executor.shutdown();
    }
    @Override
    public List<Race> getAllRaces() throws ApplicationException {
        logger.info("getAllRaces");
        Iterable<Race> iterable = raceRepository.findAll();
        List<Race> races = StreamSupport.stream(iterable.spliterator(), false).toList();
        return races ;
    }

    @Override
    public List<Participant> getAllParticipants() throws ApplicationException {
        logger.info("Obtaining all participants: " );
        Iterable<Participant> iterable = participantRepository.findAll();
        List<Participant> participants = StreamSupport.stream(iterable.spliterator(), false).toList();
        logger.info("Found participants: {}", participants);
        return participants;
    }

    @Override
    public List<ParticipantRace> getAllRegistrations() throws ApplicationException {
        logger.info("Obtaining all registrations: " );
        Iterable<ParticipantRace> iterable = registrationRepository.findAll();
        return StreamSupport.stream(iterable.spliterator(), false).toList();
    }
    @Override
    public Participant addParticipant(Participant participant) throws ApplicationException {
        logger.info("Add participant {}", participant.getName());
        Optional<Participant> optParticipant = participantRepository.save(participant);
        if (!optParticipant.isPresent()) {
            throw new ApplicationException("Could not add participant.");
        }
        notifyUsersDonator(participant);
        return participant;
    }

    @Override
    public Participant getParticipantById(Integer id){
        logger.info("Obtaining participant by id");
        return participantRepository.find(id)
                .orElseThrow(() -> new ApplicationException("Participant not found with Id:" + id));
    }

    @Override
    public Race getRaceById(Integer id){
        logger.info("Obtaining race by id");
        return raceRepository.find(id)
                .orElseThrow(() -> new ApplicationException("Race not found with Id:" + id));
    }
}
