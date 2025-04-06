package com.smarttask.repository;

import com.smarttask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by username (used for authentication)
    Optional<User> findByUsername(String username);

    // Find user by email (used during registration)
    Optional<User> findByEmail(String email);
    
 
  

}
