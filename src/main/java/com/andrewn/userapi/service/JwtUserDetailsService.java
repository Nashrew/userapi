package com.andrewn.userapi.service;
import java.util.ArrayList;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    // hardcoded username and password here since we don't actually have 'real' users in the DB

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("developer".equals(username)) {
            return new User("developer",
                    "$2a$10$FfyZ/UK135G/UY68D5pP.e/0b9CrMP7eSfxvly7A..HJDDpiWyz2y",
                    new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
