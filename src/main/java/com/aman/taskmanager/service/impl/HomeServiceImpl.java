package com.aman.taskmanager.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aman.taskmanager.entity.AccountStatus;
import com.aman.taskmanager.entity.User;
import com.aman.taskmanager.exception.ResourceNotFoundException;
import com.aman.taskmanager.repository.UserRepository;
import com.aman.taskmanager.service.HomeService;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Boolean verifyAccount(Integer uid, String code) throws Exception {

        User user = userRepository.findById(uid).orElseThrow(() -> new ResourceNotFoundException("Invalid user"));

        if (user.getAccountStatus().getVerificationCode() == null) {
            throw new Exception("User is already verified");
        }

        if (user.getAccountStatus().getVerificationCode().equals(code)) {
            AccountStatus status = user.getAccountStatus();
            status.setIsActive(true);
            status.setVerificationCode(null);
            userRepository.save(user);

            return true;
        }
        return false;
    }

}
