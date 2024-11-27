package com.aman.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aman.taskmanager.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

}
