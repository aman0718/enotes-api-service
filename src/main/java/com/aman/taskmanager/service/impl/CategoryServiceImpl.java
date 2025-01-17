package com.aman.taskmanager.service.impl;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.aman.taskmanager.dto.CategoryDto;
import com.aman.taskmanager.dto.CategoryResponse;
import com.aman.taskmanager.entity.Category;
import com.aman.taskmanager.exception.ExistsDataException;
import com.aman.taskmanager.exception.ResourceNotFoundException;
import com.aman.taskmanager.repository.CategoryRepository;
import com.aman.taskmanager.service.CategoryService;
import com.aman.taskmanager.util.Validation;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private Validation validation;

	@Override
	@Transactional
	public Boolean saveCategory(CategoryDto categoryDto) {

		// Validating
		validation.categoryValidation(categoryDto);

		Boolean existsCategory = categoryRepository.existsByName(categoryDto.getName().trim());

		if (existsCategory) {
			// throw
			throw new ExistsDataException("Category already exists");
		}

		// This is done using ModelMapper class. Converting CategoryDTO to Category
		// Map DTO to Entity
		Category category = mapper.map(categoryDto, Category.class);

		if (ObjectUtils.isEmpty(category.getId())) {
			createNewCategory(category);
		} else {
			updateCategory(category);
		}

		Category savedCategory = categoryRepository.save(category);
		return !ObjectUtils.isEmpty(savedCategory);
	}

	private void createNewCategory(Category category) {
		category.setIsDeleted(false);
		// category.setCreatedBy(1);
		// category.setCreatedOn(new Date());
	}

	private void updateCategory(Category category) {
		Optional<Category> findById = categoryRepository.findById(category.getId());

		if (findById.isPresent()) {

			Category existCategory = findById.get();

			// Keep unchanged fields from the existing entity
			category.setCreatedBy(existCategory.getCreatedBy());
			category.setCreatedOn(existCategory.getCreatedOn());
			category.setIsDeleted(existCategory.getIsDeleted());

			// Set updated fields
			// category.setUpdatedBy(1);
			// category.setUpdatedOn(new Date());
		}
	}

	@Override
	public List<CategoryDto> getAllCategory() {
		List<Category> categories = categoryRepository.findByIsDeletedFalse();

		// Converting Category to CategoryDTO
		List<CategoryDto> categoryDtoList = categories.stream().map(cat -> mapper.map(cat, CategoryDto.class)).toList();
		return categoryDtoList;
	}

	@Override
	public List<CategoryResponse> getActiveCategory() {
		List<Category> categories = categoryRepository.findByIsActiveTrueAndIsDeletedFalse();
		List<CategoryResponse> categoryList = categories.stream().map(cat -> mapper.map(cat, CategoryResponse.class))
				.toList();
		return categoryList;
	}

	@Override
	public CategoryDto getCategoryById(Integer id) throws ResourceNotFoundException {
		Category category = categoryRepository.findByIdAndIsDeletedFalse(id)
				.orElseThrow(() -> new ResourceNotFoundException("category not found with id:" + id));

		if (!ObjectUtils.isEmpty(category)) {
			return mapper.map(category, CategoryDto.class);
		}
		return null;
	}

	@Override
	public Boolean deleteCategory(Integer id) {
		Optional<Category> findCategoryById = categoryRepository.findById(id);

		if (findCategoryById.isPresent()) {
			Category category = findCategoryById.get();
			category.setIsDeleted(true);
			categoryRepository.save(category);
			return true;
		}
		return false;
	}

}
