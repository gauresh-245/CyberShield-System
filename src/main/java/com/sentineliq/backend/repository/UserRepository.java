package com.sentineliq.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sentineliq.backend.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}