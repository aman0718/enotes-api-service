package com.aman.taskmanager.service;

import com.aman.taskmanager.entity.User;

public interface JwtService {

    public String generateToken(User user);

}
