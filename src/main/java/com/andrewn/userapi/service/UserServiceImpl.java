package com.andrewn.userapi.service;

import com.andrewn.userapi.Util;
import com.andrewn.userapi.model.exceptions.UnprocessableEntityException;
import com.andrewn.userapi.model.users.User;
import com.andrewn.userapi.model.exceptions.BadRequestException;
import com.andrewn.userapi.repository.UserRepository;
import com.andrewn.userapi.repository.OffsetLimitRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final String SORT_FIELD = "lastName";

    @Autowired
    UserRepository userRepository;

    @Override
    public User getUser(Integer id) {
        return lookupUser(id);
    }

    public List<User> getUserList(Integer offset, Integer limit) {
        OffsetLimitRequest offsetLimitRequest = new OffsetLimitRequest(offset, limit, new Sort(Sort.Direction.ASC, SORT_FIELD));
        return userRepository.findAll(offsetLimitRequest).getContent();
    }

    @Override
    public User addUser(User user) {
        return saveUser(user);
    }

    @Override
    public User replaceUser(Integer id, User user) {
        User existingUser = lookupUser(id);
        BeanUtils.copyProperties(user, existingUser, "id");
        return saveUser(existingUser);
    }

    @Override
    public User updateUser(Integer id, User user) {
        User existingUser = lookupUser(id);
        BeanUtils.copyProperties(user, existingUser, Util.getNullPropertyNames(user));
        return saveUser(existingUser);
    }

    @Override
    public void deleteUser(Integer id) {
        User existingUser = lookupUser(id);
        userRepository.delete(existingUser);
    }

    private User lookupUser(Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null)
            throw new UnprocessableEntityException("User {" + id + "} does not exist.");
        return user;
    }

    private User saveUser(User user) {
        try {
            return userRepository.save(user);
        }
        catch(DataIntegrityViolationException e) {
            // Providing more generic message in order to avoid exposing SQL in an HTTP response. This would not be acceptable if more contraints were introduced,
            //      and is probably not ideal even with just the one.
            throw new UnprocessableEntityException("User with first and last name already exists");
        }
    }
}
