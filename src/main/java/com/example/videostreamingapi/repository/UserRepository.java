package com.example.videostreamingapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.videostreamingapi.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByUsername(String username);

}