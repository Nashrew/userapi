package com.andrewn.userapi.controller;

import com.andrewn.userapi.model.users.User;
import com.andrewn.userapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value = {""}, method = RequestMethod.GET)
    public ResponseEntity<List<User>> getUsers(@RequestParam(required = false, defaultValue = "0") Integer offset,
                                                  @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return new ResponseEntity<List<User>>(userService.getUserList(offset, limit), HttpStatus.OK);
    }

    @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
    public ResponseEntity<User> getUser(@PathVariable("id") Integer id) {
        return new ResponseEntity<User>(userService.getUser(id), HttpStatus.OK);
    }

    // There are many different ways to manipulate headers and differing opinions
    //  on whether or not to return the entity in post/put/patch responses.
    //  Leaving entity in the response for now because it makes for easy testing of a small demo.
    //  In the real world, it may not make sense or may be too much overhead to include in some situations.
    @RequestMapping(value = {""}, method = RequestMethod.POST)
    public ResponseEntity<User> addUser(@RequestBody User user, UriComponentsBuilder builder) {
        User savedUser = userService.addUser(user);
        HttpHeaders headers = new HttpHeaders();
        headers.add("location", "api/users/" + savedUser.getId());
        return new ResponseEntity<User>(savedUser, headers, HttpStatus.CREATED);
    }

    // Since there are no nullable fields, this put seems a little unnecessary.
    // Leaving this in because it will become relevant if nullable fields are ever introduced
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public ResponseEntity<User> replaceUser(@RequestBody User user, @PathVariable("id") Integer id) {
        return new ResponseEntity<User>( userService.replaceUser(id, user), HttpStatus.OK);
    }

    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PATCH)
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable("id") Integer id) {
        return new ResponseEntity<User>(userService.updateUser(id, user), HttpStatus.OK);
    }

    @RequestMapping(value = {"/{id}"}, method = RequestMethod.DELETE)
    public ResponseEntity deleteUser(@PathVariable("id") Integer id) {
        userService.deleteUser(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
