package com.codework.service;

import java.util.List;

import com.codework.dto.CategoryDto;
import com.codework.dto.CategoryResponse;

public interface CategoryService {

	public Boolean saveCategory(CategoryDto categoryDto);

	public List<CategoryDto> getAllCategory();

	public List<CategoryResponse> getActiveCategory();

    public CategoryDto getCategoryById(Integer id);

    public Boolean deleteCategory(Integer id);

}
