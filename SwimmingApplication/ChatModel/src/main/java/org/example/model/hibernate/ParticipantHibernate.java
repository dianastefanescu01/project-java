package org.example.model.hibernate;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Participants")
@AttributeOverride(name = "id", column = @Column(name = "participant_id"))
public class ParticipantHibernate extends EntityHibernate<Integer> {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "age")
    private int age;

    @Column(name = "user_id")
    private int userId;


    public ParticipantHibernate(String name, int age, int userId) {
        this.name = name;
        this.age = age;
        this.userId = userId;
    }

    public ParticipantHibernate() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return name + ", " + age + ", " + userId;
    }
}
