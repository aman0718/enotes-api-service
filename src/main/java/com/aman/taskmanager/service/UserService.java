package com.aman.taskmanager.service;

import com.aman.taskmanager.dto.UserDto;

public interface UserService {

    public Boolean register(UserDto userDto, String url) throws Exception;

}
