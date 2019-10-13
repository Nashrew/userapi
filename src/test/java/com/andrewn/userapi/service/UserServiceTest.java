package com.andrewn.userapi.service;

import com.andrewn.userapi.model.users.User;
import com.andrewn.userapi.repository.OffsetLimitRequest;
import com.andrewn.userapi.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetUserList() {
        List<User> users = new ArrayList<User>();
        users.add(new User(1, "123", "abc"));
        users.add(new User(2, "1234", "abcd"));
        users.add(new User(3, "12345", "abcde"));

        Page<User> userPage = new PageImpl<User>(users);
        when(userRepository.findAll(any(OffsetLimitRequest.class))).thenReturn(userPage);

        List<User> foundUsers = userService.getUserList(0, 10);

        verify(userRepository, times(1)).findAll(any(OffsetLimitRequest.class));
        verifyNoMoreInteractions(userRepository);
        assertThat(foundUsers.size()).isEqualTo(3);
    }

    @Test
    public void testGetUser() {
        User user = new User( 1, "123", "abc");
        when(userRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(user));

        User foundUser = userService.getUser(1);

        verify(userRepository, times(1)).findById(1);
        verifyNoMoreInteractions(userRepository);
        assertThat(foundUser.getId()).isEqualTo(1);
    }

    @Test
    public void testAddUser() {
        User user = new User(null, "123", "abc");
        User savedUser = new User(1, "123", "abc");
        when(userRepository.save(user)).thenReturn(savedUser);
        User saveResult = userService.addUser(user);

        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository);
        assertThat(saveResult.getId()).isEqualTo(1);
    }

    @Test
    public void testReplaceUser() {
        User existingUser = new User(1, "123", "abc");
        User newUser = new User(1, "321", "def");
        when(userRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(existingUser));
        when(userRepository.save(newUser)).thenReturn(newUser);

        User savedUser = userService.replaceUser(1, newUser);

        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).save(newUser);
        verifyNoMoreInteractions(userRepository);
        assertThat(savedUser.getId()).isEqualTo(1);
        assertThat(savedUser.getFirstName()).isEqualTo("321");
        assertThat(savedUser.getLastName()).isEqualTo("def");
    }

    @Test
    public void testUpdateUser() {
        User existingUser = new User(1, "123", "abc");
        User updateUser = new User(1, "321", null);
        User savedUser = new User(1, "321", "abc");
        when(userRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(existingUser));
        when(userRepository.save(updateUser)).thenReturn(savedUser);

        User resultUser = userService.replaceUser(1, updateUser);

        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).save(updateUser);
        verifyNoMoreInteractions(userRepository);
        assertThat(resultUser.getId()).isEqualTo(1);
        assertThat(resultUser.getFirstName()).isEqualTo("321");
        assertThat(resultUser.getLastName()).isEqualTo("abc");
    }

    @Test
    public void testDeleteUser() {
        User user = new User(1, "123", "abc");
        when(userRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(user));

        userService.deleteUser(1);

        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).delete(user);
        verifyNoMoreInteractions(userRepository);
    }

}

