package com.codework.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codework.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{

    List<Category> findByIsActiveTrue();

}
