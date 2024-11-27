package com.aman.taskmanager.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.aman.taskmanager.dto.CategoryDto;
import com.aman.taskmanager.dto.TodoDto;
import com.aman.taskmanager.dto.TodoDto.StatusDto;
import com.aman.taskmanager.enums.TodoStatus;
import com.aman.taskmanager.exception.ResourceNotFoundException;
import com.aman.taskmanager.exception.ValidationException;

@Component
public class Validation {

    public void categoryValidation(CategoryDto categoryDto) {

        Map<String, Object> error = new LinkedHashMap<>();

        if (ObjectUtils.isEmpty(categoryDto)) {
            // throw new IllegalArgumentException("Category object shouldn't be null or
            // empty");
        } else {

            // Validating name field
            if (ObjectUtils.isEmpty(categoryDto.getName())) {
                error.put("Name", "name is empty or null");
            } else {
                if (categoryDto.getName().length() < 3) {
                    error.put("Name", "name length minimum should be 3");
                }
                if (categoryDto.getName().length() > 100) {
                    error.put("Name", "name length max should be 100");
                }
            }

            // Validation description
            if (ObjectUtils.isEmpty(categoryDto.getDescription())) {
                // throw new IllegalArgumentException("desc cannot be null or empty");
                error.put("desc", "desc is empty or null");
            }

            // Validation isActive
            if (ObjectUtils.isEmpty(categoryDto.getIsActive())) {
                error.put("isActive", " isActive field is empty or null");
            } else {
                if (categoryDto.getIsActive() != Boolean.TRUE.booleanValue()
                        && categoryDto.getIsActive() != Boolean.FALSE.booleanValue()) {
                    error.put("isActive", "invalid value isActive field");
                }
                // (error.put("isActive", "isActive field is set as " +
                // categoryDto.getIsActive()));
            }
        }
        if (!error.isEmpty()) {
            throw new ValidationException(error);
        }
    }

    public void todoValidation(TodoDto todoDto) throws Exception {
        StatusDto reqStatus = todoDto.getStatus();
        Boolean statusFound = false;

        for (TodoStatus st : TodoStatus.values()) {
            if (st.getId().equals(reqStatus.getId())) {
                statusFound = true;
                break;
            }
        }

        if (!statusFound)
            throw new ResourceNotFoundException("Invalid staus");
    }
}
