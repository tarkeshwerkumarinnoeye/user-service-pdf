package com.tarkesh.learn.service;

import com.tarkesh.learn.model.User;
import java.util.List;

public interface UserService {
    List<User> getUsers(int count);
}
