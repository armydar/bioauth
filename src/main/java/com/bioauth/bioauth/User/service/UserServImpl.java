package com.bioauth.bioauth.User.service;

import com.bioauth.bioauth.User.model.Role;
import com.bioauth.bioauth.User.model.User;
import com.bioauth.bioauth.User.model.UserRegistrationDTO;
import com.bioauth.bioauth.User.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServImpl implements UserServ {
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User findByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public User findByAdmissionNumber(String admissionNumber) {
        return userRepository.findUserByAdmissionNumber(admissionNumber);
    }

    public User save(UserRegistrationDTO registration) {
        User user = new User();
        user.setAdmissionNumber(registration.getAdmissionNumber());
        user.setFirstName(registration.getFirstName());
        user.setMiddleName(registration.getMiddleName());
        user.setLastName(registration.getLastName());
        user.setEmail(registration.getEmail());
        user.setPassword(passwordEncoder.encode(registration.getPassword()));
        user.setRoles(Arrays.asList(new Role("ROLE_USER")));
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String admissionNumber) throws UsernameNotFoundException {
        User user = null;
        if (userRepository.findUserByAdmissionNumber(admissionNumber) == null) {
            user = userRepository.findUserByEmail(admissionNumber);
        } else {
            user = userRepository.findUserByAdmissionNumber(admissionNumber);
        }
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    private List<? extends GrantedAuthority> mapRolesToAuthorities(List<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
