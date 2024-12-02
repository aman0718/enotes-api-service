package com.aman.taskmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aman.taskmanager.service.HomeService;
import com.aman.taskmanager.util.CommonUtil;

@RestController
@RequestMapping("/api/v1/home")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUserAccount(@RequestParam Integer uid, @RequestParam String code) {
        try {
            Boolean verifyAccount = homeService.verifyAccount(uid, code);
            if (verifyAccount) {
                return CommonUtil.createBuildResponseMessage("Account verified successfully", HttpStatus.OK);
            }
            return CommonUtil.createErrorResponseMessage("Invalid link", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}
