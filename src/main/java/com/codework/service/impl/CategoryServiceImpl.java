package com.codework.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.codework.dto.CategoryDto;
import com.codework.dto.CategoryResponse;
import com.codework.entity.Category;
import com.codework.exception.ExistDataException;
import com.codework.exception.ResourceNotFoundException;
import com.codework.repository.CategoryRepository;
import com.codework.service.CategoryService;
import com.codework.util.Validation;

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

		// Check category exist or not
		Boolean existName = categoryRepository.existsByName(categoryDto.getName().trim());

		if (existName) {
			// throw error
			throw new ExistDataException("Category already exist");
		}

		// This is done using ModelMapper class.
		// We'll not need above 4 lines to set parameters. Converting CategoryDTO to
		// Category
		// Map DTO to Entity
		Category category = mapper.map(categoryDto, Category.class);

		if (ObjectUtils.isEmpty(category.getId())) {
			createNewCategory(category);
		} else {
			updateCategory(category);
		}

		Category saveCategory = categoryRepository.save(category);
		return !ObjectUtils.isEmpty(saveCategory);
	}

	private void createNewCategory(Category category) {
		category.setIsDeleted(false);
		category.setCreatedBy(1);
		category.setCreatedOn(new Date());
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
			category.setUpdatedBy(1);
			category.setUpdatedOn(new Date());
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
