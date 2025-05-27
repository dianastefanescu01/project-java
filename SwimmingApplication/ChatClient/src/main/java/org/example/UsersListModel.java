package org.example;

import org.example.model.User;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class UsersListModel extends AbstractListModel {
    private List<User> usersList;
    public UsersListModel() {
        this.usersList = new ArrayList<User>();
    }
    @Override
    public int getSize() {
        return usersList.size();
    }

    @Override
    public Object getElementAt(int index) {
        return usersList.get(index);
    }

    public void UserLoggedIn(User user){
        usersList.add(user);
        fireContentsChanged(this,usersList.size()-1,usersList.size());
    }

    public void UserLoggedOut(User user){
        usersList.remove(user);
        fireContentsChanged(this,0,usersList.size());
    }
}
