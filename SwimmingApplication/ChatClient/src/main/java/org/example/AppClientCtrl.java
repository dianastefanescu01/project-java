package org.example;

import org.example.model.Participant;
import org.example.model.ParticipantRace;
import org.example.model.User;
import org.example.service.ApplicationException;
import org.example.service.IAppObserver;
import org.example.service.IAppServices;

public class AppClientCtrl implements IAppObserver {
    private IAppServices appServices;
    private RegistrationsListModel registrationsListModel;
    private UsersListModel usersListModel;
    private User user;
    public AppClientCtrl(IAppServices appServices) {
        this.appServices = appServices;
        this.registrationsListModel = new RegistrationsListModel();
        this.usersListModel = new UsersListModel();
    }

    public RegistrationsListModel getRegistrationsListModel() {
        return registrationsListModel;
    }
    public UsersListModel getUsersListModel() {
        return usersListModel;
    }

    @Override
    public void userLoggedIn(User user) throws ApplicationException {
        usersListModel.UserLoggedIn(user);
    }

    @Override
    public void userLoggedOut(User user) throws ApplicationException {
        usersListModel.UserLoggedOut(user);
    }

    @Override
    public void registrationMade(ParticipantRace registration) throws ApplicationException {
        registrationsListModel.newRegistratiosn(registration);
    }

    public void logout(){
        try{
            appServices.logout(user);
        }catch(ApplicationException e){
            System.out.println("Logout error ClientCtrl"+e);
        }
    }

    public void login(String email, String password) throws ApplicationException {
        User user1=new User(email,password);
        appServices.login(user1,this);
        User[] loggedInUsers=appServices.getLoggedUsers();
        System.out.println("Logged users "+loggedInUsers.length);
        for(User user:loggedInUsers){
            usersListModel.UserLoggedIn(user);
        }
    }

    public void addRegistration(ParticipantRace registration) throws ApplicationException {
        registrationsListModel.newRegistratiosn(registration);
        appServices.addRegistration(registration);
    }

    public void addParticipant(Participant participant){

    }
}
