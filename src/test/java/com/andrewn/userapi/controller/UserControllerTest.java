package com.andrewn.userapi.controller;

import com.andrewn.userapi.model.exceptions.UnprocessableEntityException;
import com.andrewn.userapi.model.jwt.JwtRequest;
import com.andrewn.userapi.model.jwt.JwtResponse;
import com.andrewn.userapi.model.users.User;
import com.andrewn.userapi.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private String jwtToken;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        ResponseEntity<JwtResponse> response = testRestTemplate.postForEntity("http://localhost:" + port + "/auth/login",
                new JwtRequest("developer", "dev"),
                JwtResponse.class);
        jwtToken = Objects.requireNonNull(response.getBody()).getToken();
    }

    @Test
    public void testGetUsers() throws Exception {
        List<User> users = new ArrayList<User>();
        users.add(new User(1, "123", "abc"));
        users.add(new User(2, "1234", "abcd"));
        users.add(new User(3, "12345", "abcde"));

        when(userService.getUserList(0, 10)).thenReturn(users);

        mockMvc.perform(get("/api/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .content("").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void testGetUser() throws Exception {
        User user = new User(1, "123", "abc");

        when(userService.getUser(1)).thenReturn(user);

        mockMvc.perform(get("/api/users/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .content("").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3))) // 3 fields expected, id, firstName, lastName
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("123")))
                .andExpect(jsonPath("$.lastName", is("abc")));
    }

    @Test
    public void testAddUser() throws Exception {
        User user = new User("123", "abc");

        when(userService.addUser(user)).thenReturn(new User(42, "123", "abc"));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(user);

        mockMvc.perform(post("/api/users/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.*", hasSize(3))) // 3 fields expected, id, firstName, lastName
                .andExpect(jsonPath("$.id", is(42)))
                .andExpect(jsonPath("$.firstName", is("123")))
                .andExpect(jsonPath("$.lastName", is("abc")));
    }

    @Test
    public void testAddUser_duplicateName() throws Exception {
        User user = new User("123", "abc");

        when(userService.addUser(user)).thenThrow(new UnprocessableEntityException(""));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(user);

        mockMvc.perform(post("/api/users/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testUpdateUser() throws Exception {
        User existing = new User(1, "123", "abc");
        User patchRequest = new User("Aname", null);

        when(userService.getUser(1)).thenReturn(existing);
        when(userService.updateUser(1, patchRequest)).thenReturn(new User(1, "Aname", "abc"));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(patchRequest);

        mockMvc.perform(patch("/api/users/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3))) // 3 fields expected, id, firstName, lastName
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Aname")))
                .andExpect(jsonPath("$.lastName", is("abc"))); // maintained, not overwritten since not part of request
    }

    @Test
    public void testReplaceUser() throws Exception {
        User existing = new User(1, "123", "abc");
        User putRequest = new User("Aname", "Lastname");

        when(userService.getUser(1)).thenReturn(existing);
        when(userService.replaceUser(1, putRequest)).thenReturn(new User(1, "Aname", "Lastname"));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(putRequest);

        mockMvc.perform(put("/api/users/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3))) // 3 fields expected, id, firstName, lastName
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Aname")))
                .andExpect(jsonPath("$.lastName", is("Lastname"))); // maintained, not overwritten since not part of request
    }

    @Test
    public void testDeleteUser() throws Exception {
        User existing = new User(1, "123", "abc");

        when(userService.getUser(1)).thenReturn(existing);

        mockMvc.perform(delete("/api/users/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .content("").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    // Just one test for unauthorized for now, since it is a blanket
    @Test
    public void testGetUsers_unauthorized() throws Exception {
        List<User> users = new ArrayList<User>();
        users.add(new User(1, "123", "abc"));
        users.add(new User(2, "1234", "abcd"));
        users.add(new User(3, "12345", "abcde"));

        when(userService.getUserList(0, 10)).thenReturn(users);

        mockMvc.perform(get("/api/users")
                .content("").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
