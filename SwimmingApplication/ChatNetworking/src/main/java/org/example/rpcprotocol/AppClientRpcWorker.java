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
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class AppClientRpcWorker implements Runnable, IAppObserver {
    private IAppServices server;
    private Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean running;

    private static Logger logger = LogManager.getLogger(AppClientRpcWorker.class);

    public AppClientRpcWorker(IAppServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            running = true;
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                Object request = input.readObject();
                logger.debug("Received request: " + request);
                Response response = handleRequest((Request) request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }
        }

        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            logger.error("Error closing connection: " + e);
        }
    }

    private Response handleRequest(Request request) {
        Response response = null;
        if (request.type() == RequestType.LOGIN) {
            logger.debug("Login request received");
            User user = (User) request.data();
            try {
                server.login(user, this);
                return new Response.Builder().type(ResponseType.OK).build();
            } catch (ApplicationException e) {
                running = false;
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.LOGOUT) {
            logger.debug("Logout request received");
            User volunteer = (User) request.data();
            try {
                server.logout(volunteer);
                running = false;
                return new Response.Builder().type(ResponseType.OK).build();
            } catch (ApplicationException e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.ADD_REGISTRATION) {
            logger.debug("Add registration request received");
            ParticipantRace registration = (ParticipantRace) request.data();
            try {
                server.addRegistration(registration);
                //this.donationMade(donation);
                this.registrationMade(registration);
                return new Response.Builder().type(ResponseType.OK).build();
            } catch (ApplicationException e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if( request.type()==RequestType.ADD_PARTICIPANT){
            try{
                logger.debug("Add participant request received");
                Participant participant= (Participant) request.data();
                server.addParticipant(participant);
                return new Response.Builder().type(ResponseType.OK).build();
            }catch (ApplicationException e){
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if( request.type()==RequestType.GET_PARTICIPANTS) {
            try{
                logger.debug("Get participants request received");
                List<Participant> participants=server.getAllParticipants();
                return new Response.Builder().type(ResponseType.OK).data(participants).build();
            }catch (ApplicationException e){
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }

            /*try {
                logger.debug("Get participants request received");
                List<Participant> participants = server.getAllParticipants();
                return new Response.Builder()
                        .type(ResponseType.OK)
                        .data(participants)
                        .build();
            } catch (ApplicationException e) {
                return new Response.Builder()
                        .type(ResponseType.ERROR)
                        .data(e.getMessage())
                        .build();
            }

             */
        }
            if (request.type() == RequestType.GET_RACES) {
                try {
                    logger.debug("Get races request received");
                    List<Race> races = server.getAllRaces();
                    return new Response.Builder()
                            .type(ResponseType.OK)
                            .data(races)
                            .build();
                } catch (ApplicationException e) {
                    return new Response.Builder()
                            .type(ResponseType.ERROR)
                            .data(e.getMessage())
                            .build();
                }
        }
        return response;
    }

    private void sendResponse(Response response) throws IOException {
        logger.debug("Sending response: " + response);
        synchronized (output) {
            output.writeObject(response);
            output.flush();
        }
    }

    @Override
    public void userLoggedIn(User user) throws ApplicationException {
        Response response = new Response.Builder().type(ResponseType.USER_LOGGED_IN).data(user).build();
        try {
            sendResponse(response);
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }

    @Override
    public void userLoggedOut(User user) throws ApplicationException {
        Response response = new Response.Builder().type(ResponseType.USER_LOGGED_OUT).data(user).build();
        try {
            sendResponse(response);
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }

    @Override
    public void registrationMade(ParticipantRace registration) throws ApplicationException {
        Response response = new Response.Builder().type(ResponseType.REGISTRATION_MADE).data(registration).build();
        try {
            sendResponse(response);
        } catch (IOException e) {
            throw new ApplicationException("Error sending registration notification: " + e.getMessage());
        }
    }

    @Override
    public void addParticipant(Participant participant) throws ApplicationException {
        Response response = new Response.Builder().type(ResponseType.NEW_PARTICIPANT).data(participant).build();
        try {
            sendResponse(response);
        } catch (IOException e) {
            throw new ApplicationException("Error sending participant notification: " + e.getMessage());
        }
    }
}
