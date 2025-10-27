package com.github.mykyta.sirobaba.ailearningtracker.security;

import com.github.mykyta.sirobaba.ailearningtracker.constant.ErrorMessage;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;
import com.github.mykyta.sirobaba.ailearningtracker.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * Created by Mykyta Sirobaba on 16.08.2025.
 * email mykyta.sirobaba@gmail.comx
 */

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserServiceImpl userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(String.format(ErrorMessage.USER_WITH_THIS_EMAIL_NOT_FOUND, email)));
        return new CustomUserDetails(user);
    }
}