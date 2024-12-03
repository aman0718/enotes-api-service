package com.aman.taskmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aman.taskmanager.dto.LoginRequest;
import com.aman.taskmanager.dto.LoginResponse;
import com.aman.taskmanager.dto.UserDto;
import com.aman.taskmanager.service.UserService;
import com.aman.taskmanager.util.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto, HttpServletRequest httpRequest)
            throws Exception {

        String url = CommonUtil.getUrl(httpRequest);

        Boolean registerUser = userService.register(userDto, url);
        if (registerUser) {
            return CommonUtil.createBuildResponseMessage("User registered", HttpStatus.CREATED);
        }
        return CommonUtil.createErrorResponseMessage("User not registered", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        LoginResponse loginResponse = userService.login(loginRequest);
        if (ObjectUtils.isEmpty(loginResponse)) {
            return CommonUtil.createErrorResponseMessage("Invalid credentials", HttpStatus.BAD_REQUEST);
        }
        return CommonUtil.createBuildResponse(loginResponse, HttpStatus.OK);

    }

}
