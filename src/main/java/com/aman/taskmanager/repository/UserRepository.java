package com.aman.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aman.taskmanager.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    Boolean existsByEmail(String email);

    User findByEmail(String email);

}
