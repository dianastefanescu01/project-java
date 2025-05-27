package org.example.service;

import org.example.model.User;
import org.example.repository.IUserRepository;

import java.util.List;
import java.util.stream.StreamSupport;

public class UserService {
    private IUserRepository userRepository;

    public UserService(IUserRepository repository){
        this.userRepository = repository;
    }

    public void addUser(User user){
        userRepository.save(user);
    }

    public User findUser(int id){
        if(userRepository.find(id).isPresent())
            return userRepository.find(id).get();
        return null;
    }

    public List<User> findAllUsers(){
        return StreamSupport.stream(userRepository.findAll().spliterator(), false).toList();
    }

    public void deleteUser(Integer id) {
        userRepository.delete(id);
    }

    public void updateUser(User user){
        userRepository.update(user);
    }

    public User autentificate(String email, String password){
        User result = userRepository.getUserByEmailAndPass(email, password);
        return result;
    }

}
