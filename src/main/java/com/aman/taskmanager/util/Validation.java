package com.aman.taskmanager.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.aman.taskmanager.dto.CategoryDto;
import com.aman.taskmanager.dto.TodoDto;
import com.aman.taskmanager.dto.TodoDto.StatusDto;
import com.aman.taskmanager.dto.UserDto;
import com.aman.taskmanager.enums.TodoStatus;
import com.aman.taskmanager.exception.ExistsDataException;
import com.aman.taskmanager.exception.ResourceNotFoundException;
import com.aman.taskmanager.exception.ValidationException;
import com.aman.taskmanager.repository.RoleRepository;
import com.aman.taskmanager.repository.UserRepository;

@Component
public class Validation {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

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

    public void userValidation(UserDto userDto) {

        if (!StringUtils.hasText(userDto.getFirstName())) {
            throw new IllegalArgumentException("First name shouldn't be null or empty");
        }
        if (!StringUtils.hasText(userDto.getLastName())) {
            throw new IllegalArgumentException("Last name shouldn't be null or empty");
        }

        if (!StringUtils.hasText(userDto.getEmail()) || !userDto.getEmail().matches(Constants.EMAIL_REGEX)) {
            throw new IllegalArgumentException("Email shouldn't be invalid");
        } else {
            // validate email existing
            Boolean existEmail = userRepository.existsByEmail(userDto.getEmail());
            if (existEmail) {
                throw new ExistsDataException("Email already registered");
            }
        }

        // validation for password
        if (!StringUtils.hasText(userDto.getPassword())
                || !userDto.getPassword().matches(Constants.PASSWORD_REGEX)) {
            throw new IllegalArgumentException("Password should be min of 4 length with one numerical atleast");
        }

        if (!StringUtils.hasText(userDto.getMobileNumber())
                || !userDto.getMobileNumber().matches(Constants.MOBILE_NUMBER_REGEX)) {
            throw new IllegalArgumentException("Mobile number is invalid");
        }

        // Validate role
        if (CollectionUtils.isEmpty(userDto.getRoles())) {
            throw new IllegalArgumentException("Role id is invalid");
        } else {
            List<Integer> rolesIds = roleRepository.findAll()
                    .stream()
                    .map(r -> r.getId())
                    .toList();

            List<Integer> invalidRequestRoleIds = userDto.getRoles()
                    .stream()
                    .map(r -> r.getId())
                    .filter(roleId -> !rolesIds.contains((roleId)))
                    .toList();

            if (!CollectionUtils.isEmpty(invalidRequestRoleIds)) {
                throw new IllegalArgumentException("Role id is invalid " + invalidRequestRoleIds);
            }
        }
    }

}
