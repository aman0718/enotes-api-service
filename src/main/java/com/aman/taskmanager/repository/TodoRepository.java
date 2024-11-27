package com.aman.taskmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aman.taskmanager.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Integer> {

    List<Todo> findByCreatedBy(Integer userId);

}
