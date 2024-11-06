package com.codework.service.impl;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.codework.dto.CategoryDto;
import com.codework.dto.CategoryResponse;
import com.codework.entity.Category;
import com.codework.repository.CategoryRepository;
import com.codework.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ModelMapper mapper;

	@Override
	public Boolean saveCategory(CategoryDto categoryDto) {

		// This is getting done manually
		// Category category = new Category();
		// category.setName(categoryDto.getName());
		// category.setDescription(categoryDto.getDescription());
		// category.setIsActive(categoryDto.getIsActive());

		// This is done using ModelMapper class. we'll not need above 4 lines to set
		// parameters. Converting CategoryDTO to Category
		Category category = mapper.map(categoryDto, Category.class);

		category.setIsDeleted(false);
		category.setCreatedBy(1);
		category.setCreatedOn(new Date());
		Category saveCategory = categoryRepository.save(category);

		if (ObjectUtils.isEmpty(saveCategory)) {
			return false;
		}
		return true;
	}

	@Override
	public List<CategoryDto> getAllCategory() {
		List<Category> categories = categoryRepository.findAll();

		// Converting Category to CategoryDTO
		List<CategoryDto> categoryDtoList = categories.stream().map(cat -> mapper.map(cat, CategoryDto.class)).toList();
		return categoryDtoList;
	}

	@Override
	public List<CategoryResponse> getActiveCategory() {
		List<Category> categories = categoryRepository.findByIsActiveTrue();
		List<CategoryResponse> categoryList = categories.stream().map(cat -> mapper.map(cat, CategoryResponse.class))
				.toList();
		return categoryList;
	}

}
