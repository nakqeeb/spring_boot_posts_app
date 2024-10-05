package com.nakqeeb.posts_app.service;

import com.nakqeeb.posts_app.dao.UserRepository;
import com.nakqeeb.posts_app.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public boolean isUserExist(String email) {
        return this.findUserByEmail(email).isPresent();
    }
}
