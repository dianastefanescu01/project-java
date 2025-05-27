package org.example.rpcprotocol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Participant;
import org.example.model.ParticipantRace;
import org.example.model.User;
import org.example.service.ApplicationException;
import org.example.service.IAppObserver;
import org.example.service.IAppServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class AppClientRpcReflectionWorker implements Runnable, IAppObserver {
    private IAppServices server;
    private Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean running;

    private static Logger logger = LogManager.getLogger(AppClientRpcReflectionWorker.class);

    public AppClientRpcReflectionWorker(IAppServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            running = true;
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getMessage());
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
            } catch (Exception e) {
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
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }

    private static Response okResponse = new Response.Builder().type(ResponseType.OK).build();
    private static Response buildOkResponse(Object data) {
        return new Response.Builder().type(ResponseType.OK).data(data).build();
    }

    private Response handleRequest(Request request) {
        Response response = null;
        String handlerName = "handle" + (request).type();
        logger.debug("HandlerName " + handlerName);
        try {
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = (Response) method.invoke(this, request);
            logger.debug("Method " + handlerName + " invoked");
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }

        return response;
    }

    private Response handleLOGIN(Request request) {
        logger.debug("Login request ..." + request.type());
        User user = (User) request.data();
        try {
            User volunteer = server.login(user, this);
            return buildOkResponse(volunteer);
        } catch (ApplicationException e) {
            running = false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleLOGOUT(Request request) {
        logger.debug("Logout request...");
        User user = (User) request.data();
        try {
            server.logout(user);
            running = false;
            return okResponse;
        } catch (ApplicationException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    @Override
    public void userLoggedIn(User user) throws ApplicationException {
        Response response = new Response.Builder().type(ResponseType.USER_LOGGED_IN).data(user).build();
        logger.debug("User logged in " + user);
        try {
            sendResponse(response);
        } catch (IOException e) {
            throw new ApplicationException("Sending error: " + e);
        }
    }

    @Override
    public void userLoggedOut(User user) throws ApplicationException {
        Response response = new Response.Builder().type(ResponseType.USER_LOGGED_OUT).data(user).build();
        logger.debug("User logged out " + user);
        try {
            sendResponse(response);
        } catch (IOException e) {
            throw new ApplicationException("Sending error: " + e);
        }
    }

    @Override
    public void registrationMade(ParticipantRace registration) throws ApplicationException {
        Response response = new Response.Builder().type(ResponseType.REGISTRATION_MADE).data(registration).build();
        logger.debug("New donation made " + registration);
        try {
            sendResponse(response);
        } catch (IOException e) {
            throw new ApplicationException("Sending error: " + e);
        }
    }

    @Override
    public void addParticipant(Participant participant) throws ApplicationException {
        Response response = new Response.Builder().type(ResponseType.NEW_PARTICIPANT).data(participant).build();
        logger.debug("New participant added " + participant);
        try {
            sendResponse(response);
        } catch (IOException e) {
            throw new ApplicationException("Sending error: " + e);
        }
    }

    private void sendResponse(Response response) throws IOException {
        logger.debug("Sending response {}", response);
        synchronized (output) {
            output.writeObject(response);
            output.flush();
        }
    }

    private Response handleGET_PARTICIPANTS(Request request) {
        logger.debug("Get participants request...");
        try{
            Integer id = (Integer) request.data();
            Object result=server.getAllParticipants();
            return buildOkResponse(result);
        }catch (ApplicationException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleADD_REGISTRATION(Request request) {
        logger.debug("Add registration request...");
        try{
            ParticipantRace registration=(ParticipantRace) request.data();
            logger.debug("Add new registration  "+registration.toString());
            server.addRegistration(registration);
            registrationMade(registration);
            return okResponse;
        }catch (ApplicationException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleADD_PARTICIPANT(Request request) {
        logger.debug("Add participant request...");
        try{
            Participant participant=(Participant) request.data();
            logger.debug("Add new participants " + participant.toString());
            server.addParticipant(participant);
            return buildOkResponse(participant);
        }catch (ApplicationException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_REGISTRATIONS(Request request) {
        logger.debug("Get registrations request...");
        try{
            Integer id = (Integer) request.data();
            Object result=server.getAllRegistrations();
            return buildOkResponse(result);
        }catch (ApplicationException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_RACES(Request request) {
        logger.debug("Get races request...");
        try{
            Integer id = (Integer) request.data();
            //Object result=server.getRaceById(id);
            Object result = server.getAllRaces();
            return buildOkResponse(result);
        }catch (ApplicationException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }
}
