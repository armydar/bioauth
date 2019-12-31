package com.bioauth.bioauth.User.repo;

import com.bioauth.bioauth.User.model.Biometric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BioRepo extends JpaRepository<Biometric, Long> {
	
    @Query(value = "SELECT * FROM biometric WHERE user_id = :userId", nativeQuery = true)
    Biometric getUserBiometric(@Param("userId") Long userId);
    
}
