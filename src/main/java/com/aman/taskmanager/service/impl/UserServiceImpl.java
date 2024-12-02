package com.aman.taskmanager.service.impl;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.aman.taskmanager.dto.EmailRequest;
import com.aman.taskmanager.dto.UserDto;
import com.aman.taskmanager.entity.AccountStatus;
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
    public Boolean register(UserDto userDto, String url) throws Exception {

        validation.userValidation(userDto);

        User user = mapper.map(userDto, User.class);
        setRoles(userDto, user);

        AccountStatus status = AccountStatus.builder()
                .isActive(false)
                .verificationCode(UUID.randomUUID().toString())
                .build();

        user.setAccountStatus(status);

        userRepository.save(user);

        if (!ObjectUtils.isEmpty(user)) {
            // Send email
            emailSend(user, url);

            return true;
        }
        return false;
    }

    private void setRoles(UserDto userDto, User user) {

        List<Integer> reqRoleId = userDto.getRoles().stream().map(r -> r.getId()).toList();
        List<Role> roles = roleRepository.findAllById(reqRoleId);
        user.setRoles(roles);
    }

    private void emailSend(User user, String url) throws Exception {

        String messageBody = "Hi,<b>[[username]]<b/>" +
                "<br> Your account is registered with Enotes App successfully.<br>" +
                "<br> Click the below link to verify your account <br>" +
                "<a href='[[url]]'> Click Here </a> <br><br>" +
                "Thanks, <br>Enotes Services";

        messageBody = messageBody.replace("[[username]]", user.getFirstName());
        messageBody = messageBody.replace("[[url]]", url +"/api/v1/home/verify?uid=" + user.getId()
                + "&&code=" + user.getAccountStatus().getVerificationCode());

        EmailRequest emailRequest = EmailRequest.builder()
                .to(user.getEmail())
                .subject("Account Registered Successfully")
                .body(messageBody)
                .build();

        emailService.sendEmail(emailRequest);
    }

}
