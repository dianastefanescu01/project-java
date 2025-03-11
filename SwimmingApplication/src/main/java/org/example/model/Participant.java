package org.example.model;

import java.util.List;

public class Participant extends Entity<Integer>{
    private String name;
    private List<Race> raceList;
    private int age;

    public Participant(String name, List<Race> races, int age){
        this.name = name;
        this.raceList = races;
        this.age = age;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public List<Race> getRaceList(){
        return raceList;
    }

    public void setRaceList(List<Race> races){
        this.raceList = races;
    }

    public int getAge(){
        return age;
    }

    public void setAge(int age){
        this.age = age;
    }

    public String toString(){
        return getID().toString() + ',' + name.toString() + ',' + age + ',' + raceList.toString();
    }
}
