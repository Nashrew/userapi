package com.andrewn.userapi.service;

import com.andrewn.userapi.model.users.User;

import java.util.List;

public interface UserService {
    User getUser(Integer id);
    List<User> getUserList(Integer offset, Integer limit);

    User addUser(User user);
    User replaceUser(Integer id, User user);
    User updateUser(Integer id, User user);

    void deleteUser(Integer id);
}
