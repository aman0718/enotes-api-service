package com.aman.taskmanager.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.aman.taskmanager.dto.EmailRequest;
import com.aman.taskmanager.dto.UserDto;
import com.aman.taskmanager.entity.Role;
import com.aman.taskmanager.entity.User;
import com.aman.taskmanager.repository.RoleRepository;
import com.aman.taskmanager.repository.UserRepository;
import com.aman.taskmanager.service.EmailService;
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

    @Autowired
    private EmailService emailService;

    @Override
    public Boolean register(UserDto userDto) throws Exception {

        validation.userValidation(userDto);

        User user = mapper.map(userDto, User.class);
        setRoles(userDto, user);

        userRepository.save(user);

        if (!ObjectUtils.isEmpty(user)) {
            // Send email
            emailSend(userDto);

            return true;
        }
        return false;
    }

    private void setRoles(UserDto userDto, User user) {

        List<Integer> reqRoleId = userDto.getRoles().stream().map(r -> r.getId()).toList();
        List<Role> roles = roleRepository.findAllById(reqRoleId);
        user.setRoles(roles);
    }

    private void emailSend(UserDto userDto) throws Exception {

        String messageBody = "Hi,<b>" + userDto.getFirstName() +"<b/>"+
                "<br> Your account is registered with Enotes App successfully.<br>" +
                "<br> Click the below link to verify your account <br>" +
                "<a href='#'> Click Here </a> <br><br>" +
                "Thanks, <br>Enotes Services";

        EmailRequest emailRequest = EmailRequest.builder()
                .to(userDto.getEmail())
                .subject("Account Registered Successfully")
                .body(messageBody)
                .build();

        emailService.sendEmail(emailRequest);
    }

}
