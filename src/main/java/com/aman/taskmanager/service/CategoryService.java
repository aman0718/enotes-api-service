package com.aman.taskmanager.service;

import java.util.List;

import com.aman.taskmanager.dto.CategoryDto;
import com.aman.taskmanager.dto.CategoryResponse;
import com.aman.taskmanager.exception.ResourceNotFoundException;

public interface CategoryService {

	public Boolean saveCategory(CategoryDto categoryDto);

	public List<CategoryDto> getAllCategory();

	public List<CategoryResponse> getActiveCategory();

    public CategoryDto getCategoryById(Integer id) throws ResourceNotFoundException;

    public Boolean deleteCategory(Integer id);

}
