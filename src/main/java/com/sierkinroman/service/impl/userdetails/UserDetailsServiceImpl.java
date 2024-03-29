package com.sierkinroman.service.impl.userdetails;

import com.sierkinroman.entities.User;
import com.sierkinroman.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        if (user == null) {
            log.info("Can't load user, User with username '{}' is not found", username);
            throw new UsernameNotFoundException("User with username: '" + username + "' is not found");
        }
        return new UserDetailsImpl(user);
    }

}
