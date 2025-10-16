package com.finance.controller;

import com.finance.domain.User;
import com.finance.form.UserRegistrationForm;
import com.finance.form.PasswordChangeForm;
import com.finance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;

/**
 * User Controller - Web Layer for User Management Operations
 *
 * This controller handles all HTTP requests related to user management including
 * registration, authentication, profile management, and password changes.
 * It serves as the primary interface between the web layer and business logic layer.
 *
 * Key Responsibilities:
 * - User registration workflow with validation
 * - User profile display and editing
 * - Password change functionality with security validation
 * - Dashboard access and user-specific content
 * - Form validation and error handling
 * - Redirect management and flash messaging
 *
 * Security Features:
 * - Principal-based user identification for authenticated operations
 * - Form validation with server-side error handling
 * - Access control through Spring Security integration
 * - CSRF protection through Spring Security
 *
 * URL Mappings:
 * - GET/POST /user/register - User registration
 * - GET /user/profile - View user profile
 * - GET/POST /user/edit - Edit user profile
 * - GET/POST /user/change-password - Password management
 * - GET /user/dashboard - User dashboard (legacy)
 *
 * @author Personal Finance Manager Team
 * @version 1.0.0
 * @since 2024
 */

@Controller
@RequestMapping("/user")
public class UserController {

    // ================================
    // DEPENDENCIES
    // ================================

    /**
     * Service layer for user-related business operations.
     * Handles user creation, validation, profile management, and authentication.
     */
    @Autowired
    private UserService userService;

    // ================================
    // USER REGISTRATION
    // ================================

    /**
     * Displays the user registration form.
     *
     * @param model Spring MVC model for passing data to the view
     * @return The registration form view name
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userForm", new UserRegistrationForm());
        return "register";
    }

    /**
     * Processes user registration form submission.
     * Validates form data, checks for duplicate users, and creates new account.
     *
     * @param userForm The registration form data with validation annotations
     * @param bindingResult Spring validation results
     * @param redirectAttributes For flash messages on redirect
     * @return Redirect to login on success, or back to registration form on error
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userForm") UserRegistrationForm userForm,
                              BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        // Validate password confirmation match
        if (!userForm.isPasswordMatching()) {
            bindingResult.rejectValue("confirmPassword", "error.userForm", "Passwords do not match");
        }

        // Check for duplicate username
        if (userService.existsByUsername(userForm.getUsername())) {
            bindingResult.rejectValue("username", "error.userForm", "Username already exists");
        }

        // Check for duplicate email
        if (userService.existsByEmail(userForm.getEmail())) {
            bindingResult.rejectValue("email", "error.userForm", "Email already exists");
        }

        // Return to form if validation errors exist
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            // Create new user account
            userService.createUser(
                userForm.getUsername(),
                userForm.getEmail(),
                userForm.getPassword()
            );

            // Success: redirect to login with success message
            redirectAttributes.addFlashAttribute("successMessage",
                "Registration successful! Please login with your new account.");
            return "redirect:/login";

        } catch (Exception e) {
            // Handle unexpected errors during registration
            bindingResult.reject("registrationError",
                "An error occurred during registration: " + e.getMessage());
            return "register";
        }
    }

    // ================================
    // USER DASHBOARD AND PROFILE
    // ================================

    /**
     * Displays the user dashboard (legacy endpoint).
     * Note: This endpoint is maintained for backward compatibility.
     * New implementations should use /dashboard directly.
     *
     * @param principal Spring Security principal containing authenticated user info
     * @param model Spring MVC model for passing data to the view
     * @return The dashboard view name
     * @throws RuntimeException if authenticated user is not found in database
     */
    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + principal.getName()));

        model.addAttribute("user", user);
        model.addAttribute("username", principal.getName());

        return "dashboard";
    }

    /**
     * Displays the user's profile information.
     * Shows read-only view of user details including username, email, and account info.
     *
     * @param principal Spring Security principal containing authenticated user info
     * @param model Spring MVC model for passing data to the view
     * @return The profile view name
     * @throws RuntimeException if authenticated user is not found in database
     */
    @GetMapping("/profile")
    public String showProfile(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + principal.getName()));

        model.addAttribute("user", user);
        return "user/profile";
    }

    /**
     * Displays the profile editing form.
     * Allows users to modify their username and email address.
     *
     * @param principal Spring Security principal containing authenticated user info
     * @param model Spring MVC model for passing data to the view
     * @return The profile editing form view name
     * @throws RuntimeException if authenticated user is not found in database
     */
    @GetMapping("/edit")
    public String showEditProfile(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + principal.getName()));

        model.addAttribute("user", user);
        return "user/edit-profile";
    }

    /**
     * Processes profile update form submission.
     * Validates ownership, uniqueness constraints, and updates user information.
     *
     * @param user The updated user data from the form
     * @param bindingResult Spring validation results
     * @param principal Spring Security principal containing authenticated user info
     * @param redirectAttributes For flash messages on redirect
     * @return Redirect to profile on success, or back to edit form on error
     */
    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult, Principal principal,
                               RedirectAttributes redirectAttributes) {

        // Retrieve current authenticated user for security validation
        User currentUser = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + principal.getName()));

        // Security check: ensure user can only update their own profile
        if (!currentUser.getId().equals(user.getId())) {
            return "redirect:/access-denied";
        }

        // Validate username uniqueness (excluding current user)
        if (userService.existsByUsername(user.getUsername()) &&
            !currentUser.getUsername().equals(user.getUsername())) {
            bindingResult.rejectValue("username", "error.user", "Username already exists");
        }

        // Validate email uniqueness (excluding current user)
        if (userService.existsByEmail(user.getEmail()) &&
            !currentUser.getEmail().equals(user.getEmail())) {
            bindingResult.rejectValue("email", "error.user", "Email already exists");
        }

        // Return to form if validation errors exist
        if (bindingResult.hasErrors()) {
            return "user/edit-profile";
        }

        try {
            // Update user profile through service layer
            userService.updateProfile(currentUser.getId(), user);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/user/profile";

        } catch (Exception e) {
            // Handle unexpected errors during profile update
            bindingResult.reject("updateError",
                "An error occurred while updating profile: " + e.getMessage());
            return "user/edit-profile";
        }
    }

    // ================================
    // PASSWORD MANAGEMENT
    // ================================

    /**
     * Displays the password change form.
     * Allows authenticated users to change their account password.
     *
     * @param model Spring MVC model for passing data to the view
     * @return The password change form view name
     */
    @GetMapping("/change-password")
    public String showChangePassword(Model model) {
        model.addAttribute("passwordForm", new PasswordChangeForm());
        return "user/change-password";
    }

    /**
     * Processes password change form submission.
     * Validates current password, confirms new password match, and updates password.
     *
     * @param form The password change form data with validation
     * @param bindingResult Spring validation results
     * @param principal Spring Security principal containing authenticated user info
     * @param redirectAttributes For flash messages on redirect
     * @return Redirect to profile on success, or back to password form on error
     */
    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("passwordForm") PasswordChangeForm form,
                                BindingResult bindingResult, Principal principal,
                                RedirectAttributes redirectAttributes) {

        // Validate new password confirmation match
        if (!form.isPasswordMatching()) {
            bindingResult.rejectValue("confirmNewPassword", "error.passwordForm",
                "New password and confirmation do not match");
        }

        // Return to form if validation errors exist
        if (bindingResult.hasErrors()) {
            return "user/change-password";
        }

        try {
            // Retrieve current user for password change operation
            User user = userService.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + principal.getName()));

            // Change password through service layer (includes current password validation)
            userService.changePassword(user.getId(), form.getCurrentPassword(), form.getNewPassword());

            // Success: redirect to profile with success message
            redirectAttributes.addFlashAttribute("successMessage",
                "Password changed successfully! Please use your new password for future logins.");
            return "redirect:/user/profile";

        } catch (Exception e) {
            // Handle password change errors (invalid current password, etc.)
            bindingResult.reject("passwordError", e.getMessage());
            return "user/change-password";
        }
    }
}