package com.codework.service;

import java.util.List;

import com.codework.entity.Category;

public interface CategoryService {
	
	public Boolean saveCategory(Category category);
	public List<Category> getAllCategory();

}
