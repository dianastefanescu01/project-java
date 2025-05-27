package org.example.model;

import java.io.Serializable;

public class Participant extends Entity<Integer> implements Serializable {
    private String name;
    private int age;
    private int userId;

    public Participant(String name, int age, int userId){
        this.name = name;
        this.age = age;
        this.userId = userId;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getAge(){
        return age;
    }

    public void setAge(int age){
        this.age = age;
    }

    public int getUserId(){
        return this.userId;
    }

    public void setUserId(int userId){
        this.userId = userId;
    }

    @Override
    public String toString(){
        return name.toString() + ',' + age + ',' + userId;
    }
}
