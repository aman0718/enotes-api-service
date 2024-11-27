package com.aman.taskmanager.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.aman.taskmanager.dto.UserDto;
import com.aman.taskmanager.entity.Role;
import com.aman.taskmanager.entity.User;
import com.aman.taskmanager.repository.RoleRepository;
import com.aman.taskmanager.repository.UserRepository;
import com.aman.taskmanager.service.UserService;
import com.aman.taskmanager.util.Validation;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private Validation validation;

    @Override
    public Boolean register(UserDto userDto) {

        validation.userValidation(userDto);

        User user = mapper.map(userDto, User.class);
        setRoles(userDto, user);

        userRepository.save(user);

        if (!ObjectUtils.isEmpty(user)) {
            return true;
        }
        return false;
    }

    private void setRoles(UserDto userDto, User user) {

        List<Integer> reqRoleId = userDto.getRoles().stream().map(r -> r.getId()).toList();
        List<Role> roles = roleRepository.findAllById(reqRoleId);
        user.setRoles(roles);
    }

}
