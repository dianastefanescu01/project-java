package org.example.rpcprotocol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Participant;
import org.example.model.ParticipantRace;
import org.example.model.Race;
import org.example.model.User;
import org.example.service.ApplicationException;
import org.example.service.IAppObserver;
import org.example.service.IAppServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.IDN;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AppServicesRpcProxy implements IAppServices {

    private String host;
    private int port;
    private static Logger logger = LogManager.getLogger(AppServicesRpcProxy.class);

    private IAppObserver client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public AppServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<>();
    }

    @Override
    public User login(User user, IAppObserver client) throws ApplicationException {
        initializeConnection();
        Request req = new Request.Builder().type(RequestType.LOGIN).data(user).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            this.client=client;
            return (User) response.data();
        }
        else {
            closeConnection();
            throw new ApplicationException(response.data().toString());
        }
    }

    @Override
    public void logout(User user) throws ApplicationException {
        Request req = new Request.Builder().type(RequestType.LOGOUT).data(user).build();
        sendRequest(req);
        Response response = readResponse();
        closeConnection();
        if (response.type() == ResponseType.ERROR) {
            throw new ApplicationException(response.data().toString());
        }
    }

    @Override
    public Participant getParticipantById(Integer id) throws ApplicationException{
        Request request=new Request.Builder().type(RequestType.GET_PARTICIPANTS).data(id).build();
        sendRequest(request);
        Response response=readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new ApplicationException(response.data().toString());
        }
        return (Participant) response.data();
    }

    @Override
    public Race getRaceById(Integer id) throws ApplicationException{
        Request request=new Request.Builder().type(RequestType.GET_RACES).data(id).build();
        sendRequest(request);
        Response response=readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new ApplicationException(response.data().toString());
        }
        return (Race) response.data();
    }

    @Override
    public List<Race> getAllRaces() throws ApplicationException {
        Request req = new Request.Builder()
                .type(RequestType.GET_RACES)
                .build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new ApplicationException(response.data().toString());
        }
        return (List<Race>)response.data();
    }

    @Override
    public List<Participant> getAllParticipants() throws ApplicationException {
        Request request=new Request.Builder().type(RequestType.GET_PARTICIPANTS).build();
        sendRequest(request);
        Response response=readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new ApplicationException(response.data().toString());
        }
        return (List<Participant>) response.data();
    }

    @Override
    public Participant addParticipant(Participant participant) throws ApplicationException {
        Request request=new Request.Builder().type(RequestType.ADD_PARTICIPANT).data(participant).build();
        sendRequest(request);
        Response response=readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new ApplicationException(response.data().toString());
        }
        return (Participant) response.data();
    }

    @Override
    public List<ParticipantRace> getAllRegistrations() throws ApplicationException {
        Request request=new Request.Builder().type(RequestType.GET_REGISTRATIONS).build();
        sendRequest(request);
        Response response=readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new ApplicationException(response.data().toString());
        }
        return (List<ParticipantRace>) response.data();
    }


    @Override
    public void addRegistration(ParticipantRace registration) throws ApplicationException {
        Request req = new Request.Builder().type(RequestType.ADD_REGISTRATION).data(registration).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new ApplicationException(response.data().toString());
        }
    }

    @Override
    public User[] getLoggedUsers() throws ApplicationException {
        Request req = new Request.Builder().type(RequestType.GET_LOGGED_USERS).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new ApplicationException(response.data().toString());
        }
        return (User []) response.data();
    }

    private void closeConnection() {
        logger.debug("Closing connection");
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            logger.error("Error closing connection: ", e);
        }
    }

    private void sendRequest(Request request) throws ApplicationException {
//        initializeConnection();
        logger.debug("Sending request: {}", request);
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new ApplicationException("Error sending request: " + e.getMessage());
        }
    }

    private Response readResponse() throws ApplicationException {
        try {
            return qresponses.take();
        } catch (InterruptedException e) {
            throw new ApplicationException("Error reading response: " + e.getMessage());
        }
    }

    private void initializeConnection() throws ApplicationException {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            throw new ApplicationException("Error initializing connection: " + e.getMessage());
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    private void handleUpdate(Response response) {
        logger.debug("Update response: {}", response);
        if (client == null) {
            logger.error("Client is null. Cannot process update.");
            return;
        }
        if (response.type() == ResponseType.USER_LOGGED_IN) {
            User user = (User) response.data();
            logger.debug("User logged in: {}", user);
            try {
                client.userLoggedIn(user);
            } catch (ApplicationException e) {
                logger.error("Error handling user login update: ", e);
            }
        } else if (response.type() == ResponseType.USER_LOGGED_OUT) {
            User user = (User) response.data();
            logger.debug("User logged out: {}", user);
            try {
                client.userLoggedOut(user);
            } catch (ApplicationException e) {
                logger.error("Error handling user logout update: ", e);
            }
        } else if (response.type() == ResponseType.REGISTRATION_MADE) {
            ParticipantRace registration = (ParticipantRace) response.data();
            logger.debug("Registration made: {}", registration);
            try {
                client.registrationMade(registration);
            } catch (ApplicationException e) {
                logger.error("Error handling registration update: ", e);
            }
        } else if (response.type() == ResponseType.NEW_PARTICIPANT) {
            Participant participant = (Participant) response.data();
            logger.debug("Participant: {}", participant);
            try {
                client.addParticipant(participant);
            } catch (ApplicationException e) {
                logger.error("Error handling participant insertion: ", e);
            }
        }

    }

    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.REGISTRATION_MADE ||
                response.type() == ResponseType.NEW_PARTICIPANT;
    }

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Object response=input.readObject();
                    logger.debug("response received "+response);
                    if (isUpdate((Response)response)){
                        handleUpdate((Response)response);
                    }else{
                        try {
                            qresponses.put((Response)response);
                        } catch (InterruptedException e) {
                            logger.error(e);
                            logger.error(e.getStackTrace());
                        }
                    }
                } catch (IOException|ClassNotFoundException e) {
                    logger.error("Reading error "+e);
                }
            }
        }
    }
}
