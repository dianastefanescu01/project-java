package org.example.model.hibernate;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table (name = "Users")
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class UserHibernate extends EntityHibernate<Integer>implements Serializable {

    @Column(name = "username", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    public UserHibernate() {
        // Required by Hibernate
    }

    public UserHibernate(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public UserHibernate(String email, String password) {
        this.email = email;
        this.password = password;
        this.name = "";
    }

    public UserHibernate(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return name + "," + email + "," + password;
    }
}
