package com.codework.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codework.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{

}
