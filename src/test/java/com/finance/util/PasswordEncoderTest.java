package com.finance.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {

    @Test
    public void generatePasswordHashes() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String adminPassword = "admin123";
        String userPassword = "user123";
        
        String adminHash = encoder.encode(adminPassword);
        String userHash = encoder.encode(userPassword);
        
        System.out.println("Admin password hash for 'admin123': " + adminHash);
        System.out.println("User password hash for 'user123': " + userHash);
        
        // Verify the hashes work
        System.out.println("Admin hash verification: " + encoder.matches(adminPassword, adminHash));
        System.out.println("User hash verification: " + encoder.matches(userPassword, userHash));
    }
}
