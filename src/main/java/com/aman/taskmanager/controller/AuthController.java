package com.aman.taskmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aman.taskmanager.dto.UserDto;
import com.aman.taskmanager.service.UserService;
import com.aman.taskmanager.util.CommonUtil;

@RestController
@RequestMapping("/api/v1/user")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        
        Boolean registerUser = userService.register(userDto);
        if (registerUser) {
            return CommonUtil.createBuildResponseMessage("User registered", HttpStatus.CREATED);
        }
        return CommonUtil.createErrorResponseMessage("User not registered", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
