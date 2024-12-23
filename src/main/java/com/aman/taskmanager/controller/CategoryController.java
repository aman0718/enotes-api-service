package com.aman.taskmanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aman.taskmanager.dto.CategoryDto;
import com.aman.taskmanager.dto.CategoryResponse;
import com.aman.taskmanager.exception.ResourceNotFoundException;
import com.aman.taskmanager.service.CategoryService;
import com.aman.taskmanager.util.CommonUtil;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@PostMapping("/save")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> saveCategory(@RequestBody CategoryDto categoryDto) {

		Boolean saveCategory = categoryService.saveCategory(categoryDto);

		if (saveCategory) {
			return CommonUtil.createBuildResponseMessage("saved Success", HttpStatus.CREATED);
			// return new ResponseEntity<>(, HttpStatus.CREATED);
		} else {
			return CommonUtil.createErrorResponseMessage("not saved", HttpStatus.INTERNAL_SERVER_ERROR);
			// return new ResponseEntity<>("not saved", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// This end-point is for admin
	@GetMapping("/")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getAllCategory() {

		List<CategoryDto> categories = categoryService.getAllCategory();

		if (CollectionUtils.isEmpty(categories)) {
			// return CommonUtil.createBuildResponse(categories, HttpStatus.NO_CONTENT);
			return ResponseEntity.noContent().build();
		} else {
			return CommonUtil.createBuildResponse(categories, HttpStatus.OK);
			// return new ResponseEntity<>(categories, HttpStatus.OK);
		}

	}

	// This end-point is for client
	@GetMapping("/active")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> getActiveCategory() {
 
		List<CategoryResponse> categories = categoryService.getActiveCategory();

		if (CollectionUtils.isEmpty(categories))
			return ResponseEntity.noContent().build();
		else
			return CommonUtil.createBuildResponse(categories, HttpStatus.OK);
		// return new ResponseEntity<>(categories, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<?> getCategoryDetailsById(@PathVariable Integer id) {

		try {
			CategoryDto categoryDto = categoryService.getCategoryById(id);
			if (ObjectUtils.isEmpty(categoryDto)) {
				// return new ResponseEntity<>("internal server Error", HttpStatus.NOT_FOUND);
				return CommonUtil.createErrorResponseMessage("Internal Server Error", HttpStatus.NOT_FOUND);
			}
			return CommonUtil.createBuildResponse(categoryDto, HttpStatus.OK);
			// return new ResponseEntity<>(categoryDto, HttpStatus.OK);

		} catch (ResourceNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<?> deleteCategoryById(@PathVariable Integer id) {

		Boolean deleted = categoryService.deleteCategory(id);
		if (deleted) {
			// return new ResponseEntity<>("Category deleted successfully:" + id,
			// HttpStatus.OK);
			return CommonUtil.createBuildResponse("category deleted successfully", HttpStatus.OK);
		}
		// return new ResponseEntity<>("Category not deleted",
		// HttpStatus.INTERNAL_SERVER_ERROR);
		return CommonUtil.createErrorResponseMessage("Category not created", HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
