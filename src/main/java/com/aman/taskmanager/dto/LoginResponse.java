package com.aman.taskmanager.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private UserDto userDto;
    private String token;

}
