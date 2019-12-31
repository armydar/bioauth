package com.bioauth.bioauth.User.repo;

import com.bioauth.bioauth.User.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findUserByAdmissionNumber(String admissionNumber);
    User findUserByEmail(String email);
}
