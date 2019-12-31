package com.bioauth.bioauth.User.service;

import com.bioauth.bioauth.User.model.User;
import com.bioauth.bioauth.User.model.UserRegistrationDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserServ extends UserDetailsService {
    public User findByEmail(String email);
    public User findByAdmissionNumber(String admissionNumber);
    public User save(UserRegistrationDTO registration);
}
