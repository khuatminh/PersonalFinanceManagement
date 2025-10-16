package com.finance.service;

import com.finance.domain.Role;
import com.finance.domain.User;
import com.finance.repository.RoleRepository;
import com.finance.repository.UserRepository;
import com.finance.exception.UserNotFoundException;
import com.finance.exception.DuplicateUserException;
import com.finance.exception.InvalidPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * User Service - Business Logic Layer for User Management
 *
 * This service class encapsulates all business logic related to user operations
 * including user creation, authentication, profile management, and user queries.
 * It serves as the primary interface between the controller layer and data access layer.
 *
 * Key Responsibilities:
 * - User registration and validation
 * - Password encryption and verification
 * - User profile management
 * - User search and retrieval operations
 * - Role assignment and authorization support
 * - Data integrity and business rule enforcement
 *
 * Security Features:
 * - BCrypt password encryption for all user passwords
 * - Duplicate username/email validation
 * - Password strength validation through form validation
 * - Secure password change operations with current password verification
 *
 * Transaction Management:
 * - All write operations are wrapped in database transactions
 * - Ensures data consistency and rollback capability on errors
 *
 * @author Personal Finance Manager Team
 * @version 1.0.0
 * @since 2024
 */

@Service
@Transactional
public class UserService {

    // ================================
    // DEPENDENCIES
    // ================================

    /**
     * Repository for user data access operations.
     * Provides CRUD operations and custom query methods for User entities.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Repository for role data access operations.
     * Used for role assignment and authorization queries.
     */
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Spring Security password encoder for secure password handling.
     * Uses BCrypt algorithm for password hashing and verification.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    // ================================
    // BASIC CRUD OPERATIONS
    // ================================

    /**
     * Retrieves all users from the database.
     *
     * @return List of all users in the system
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Finds a user by their unique identifier.
     *
     * @param id The user ID to search for
     * @return Optional containing the user if found, empty otherwise
     * @throws IllegalArgumentException if id is null
     */
    public Optional<User> findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return userRepository.findById(id);
    }

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for
     * @return Optional containing the user if found, empty otherwise
     * @throws IllegalArgumentException if username is null or empty
     */
    public Optional<User> findByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return userRepository.findByUsername(username);
    }

    /**
     * Finds a user by their email address.
     *
     * @param email The email address to search for
     * @return Optional containing the user if found, empty otherwise
     * @throws IllegalArgumentException if email is null or empty
     */
    public Optional<User> findByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return userRepository.findByEmail(email);
    }

    /**
     * Saves a user to the database.
     * For new users, encrypts the password before saving.
     *
     * @param user The user to save
     * @return The saved user entity
     * @throws IllegalArgumentException if user is null
     */
    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // Encrypt password for new users only
        if (user.getId() == null && StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }

    // ================================
    // USER REGISTRATION AND MANAGEMENT
    // ================================

    /**
     * Creates a new user account with the provided credentials.
     * Validates uniqueness of username and email before creation.
     * Assigns default USER role and encrypts the password.
     *
     * @param username The desired username (must be unique)
     * @param email The user's email address (must be unique)
     * @param password The raw password (will be encrypted)
     * @return The newly created user
     * @throws DuplicateUserException if username or email already exists
     * @throws IllegalArgumentException if any parameter is null or empty
     * @throws RuntimeException if USER role is not found in the system
     */
    public User createUser(String username, String email, String password) {
        // Validate input parameters
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!StringUtils.hasText(password)) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        // Check for duplicate username
        if (userRepository.existsByUsername(username)) {
            throw DuplicateUserException.forUsername(username);
        }

        // Check for duplicate email
        if (userRepository.existsByEmail(email)) {
            throw DuplicateUserException.forEmail(email);
        }

        // Create new user entity
        User user = new User();
        user.setUsername(username.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(password));

        // Assign default USER role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found in system"));
        user.setUserRole(userRole);

        return userRepository.save(user);
    }

    /**
     * Updates an existing user's information.
     * Allows updating username, email, role, and optionally password.
     *
     * @param id The ID of the user to update
     * @param userDetails The updated user information
     * @return The updated user entity
     * @throws UserNotFoundException if user with given ID doesn't exist
     * @throws IllegalArgumentException if id or userDetails is null
     */
    public User updateUser(Long id, User userDetails) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (userDetails == null) {
            throw new IllegalArgumentException("User details cannot be null");
        }

        return userRepository.findById(id)
                .map(user -> {
                    // Update basic information
                    if (StringUtils.hasText(userDetails.getUsername())) {
                        user.setUsername(userDetails.getUsername().trim());
                    }
                    if (StringUtils.hasText(userDetails.getEmail())) {
                        user.setEmail(userDetails.getEmail().trim().toLowerCase());
                    }
                    if (userDetails.getUserRole() != null) {
                        user.setUserRole(userDetails.getUserRole());
                    }

                    // Update password only if provided and not empty
                    if (StringUtils.hasText(userDetails.getPassword())) {
                        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                    }

                    return userRepository.save(user);
                })
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete
     * @throws UserNotFoundException if user with given ID doesn't exist
     * @throws IllegalArgumentException if id is null
     */
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    // ================================
    // VALIDATION AND QUERY METHODS
    // ================================

    /**
     * Checks if a username already exists in the system.
     *
     * @param username The username to check
     * @return true if username exists, false otherwise
     * @throws IllegalArgumentException if username is null or empty
     */
    public boolean existsByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return userRepository.existsByUsername(username);
    }

    /**
     * Checks if an email address already exists in the system.
     *
     * @param email The email to check
     * @return true if email exists, false otherwise
     * @throws IllegalArgumentException if email is null or empty
     */
    public boolean existsByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return userRepository.existsByEmail(email);
    }

    /**
     * Searches for users by username or email containing the given keyword.
     *
     * @param keyword The search keyword
     * @return List of users matching the search criteria
     * @throws IllegalArgumentException if keyword is null or empty
     */
    public List<User> searchUsers(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            throw new IllegalArgumentException("Search keyword cannot be null or empty");
        }
        return userRepository.findByUsernameOrEmailContaining(keyword.trim());
    }

    // ================================
    // STATISTICS AND REPORTING
    // ================================

    /**
     * Gets the total number of users in the system.
     *
     * @return Total user count
     */
    public long getUserCount() {
        return userRepository.count();
    }

    /**
     * Gets the number of users with admin privileges.
     *
     * @return Admin user count
     */
    public long getAdminCount() {
        return userRepository.countByAdminRole();
    }

    /**
     * Gets the number of regular (non-admin) users.
     *
     * @return Regular user count
     */
    public long getRegularUserCount() {
        return userRepository.countByUserRole();
    }

    // ================================
    // AUTHENTICATION AND SECURITY
    // ================================

    /**
     * Validates a raw password against an encoded password.
     * Uses BCrypt algorithm for secure password verification.
     *
     * @param rawPassword The plain text password to validate
     * @param encodedPassword The encoded password to compare against
     * @return true if passwords match, false otherwise
     * @throws IllegalArgumentException if either parameter is null or empty
     */
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        if (!StringUtils.hasText(rawPassword)) {
            throw new IllegalArgumentException("Raw password cannot be null or empty");
        }
        if (!StringUtils.hasText(encodedPassword)) {
            throw new IllegalArgumentException("Encoded password cannot be null or empty");
        }
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Updates a user's profile information (username and email only).
     * Password and role changes are handled by separate methods for security.
     *
     * @param userId The ID of the user to update
     * @param userDetails The updated profile information
     * @throws UserNotFoundException if user with given ID doesn't exist
     * @throws IllegalArgumentException if userId or userDetails is null
     */
    public void updateProfile(Long userId, User userDetails) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (userDetails == null) {
            throw new IllegalArgumentException("User details cannot be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Update only profile information, not sensitive data
        if (StringUtils.hasText(userDetails.getUsername())) {
            user.setUsername(userDetails.getUsername().trim());
        }
        if (StringUtils.hasText(userDetails.getEmail())) {
            user.setEmail(userDetails.getEmail().trim().toLowerCase());
        }

        // Note: Password and role changes are handled by separate methods for security

        userRepository.save(user);
    }

    /**
     * Changes a user's password after validating the current password.
     * Implements secure password change workflow with current password verification.
     *
     * @param userId The ID of the user changing their password
     * @param currentPassword The user's current password for verification
     * @param newPassword The new password to set
     * @throws UserNotFoundException if user with given ID doesn't exist
     * @throws InvalidPasswordException if current password is incorrect
     * @throws IllegalArgumentException if any parameter is null or empty
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (!StringUtils.hasText(currentPassword)) {
            throw new IllegalArgumentException("Current password cannot be null or empty");
        }
        if (!StringUtils.hasText(newPassword)) {
            throw new IllegalArgumentException("New password cannot be null or empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Verify current password before allowing change
        if (!validatePassword(currentPassword, user.getPassword())) {
            throw InvalidPasswordException.incorrectCurrentPassword();
        }

        // Set new encrypted password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}