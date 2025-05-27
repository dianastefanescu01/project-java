package org.example.model;

import java.io.Serializable;

public class User extends Entity<Integer> implements Serializable {
    private String name;
    private String email;
    private String password;

    public User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String email, String password){
        this.email = email;
        this.password = password;
        this.name = "";
    }

    public User(String email){
        this.email = email;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    @Override
    public String toString(){
        return name.toString() + ',' + email.toString() + ',' + password.toString();
    }
}
