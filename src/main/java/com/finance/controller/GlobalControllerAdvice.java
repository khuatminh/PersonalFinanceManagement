package com.finance.controller;

import com.finance.domain.User;
import com.finance.service.NotificationService;
import com.finance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @ModelAttribute("unreadNotificationCount")
    public long getUnreadNotificationCount(Principal principal) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName())
                    .orElse(null);
            if (user != null) {
                return notificationService.getUnreadNotificationCount(user);
            }
        }
        return 0;
    }
}

