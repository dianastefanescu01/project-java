package org.example.repository;

import org.example.model.User;

public interface IUserRepository extends IRepository<Integer,User>  {
    public User getUserByEmailAndPass(String email, String password);
}
