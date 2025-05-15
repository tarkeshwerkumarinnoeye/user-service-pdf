package com.tarkesh.learn.service.impl;

import com.tarkesh.learn.model.User;
import com.tarkesh.learn.repository.UserRepository;
import com.tarkesh.learn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getUsers(int count) {
        return userRepository.getSampleUsers(count);
    }
}
