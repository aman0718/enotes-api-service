package com.aman.taskmanager.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

import com.aman.taskmanager.entity.User;
import com.aman.taskmanager.util.CommonUtil;

public class AuditAwareConfig implements AuditorAware<Integer> {

    @Override
    public Optional<Integer> getCurrentAuditor() {

        User loggedInUser = CommonUtil.getLoggedInUser();
        return Optional.of(loggedInUser.getId());
    }

}
