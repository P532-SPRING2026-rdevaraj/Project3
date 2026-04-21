package com.tracker.manager;

import com.tracker.domain.AppUser;
import com.tracker.domain.UserRole;
import com.tracker.resourceaccess.AppUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Manager layer — manages AppUser entities (Change 3).
 * No authentication: just CRUD so the UI login dropdown can select a user.
 */
@Service
public class UserManager {

    private final AppUserRepository userRepository;

    public UserManager(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<AppUser> listAll() {
        return userRepository.findAll();
    }

    public AppUser create(String username, UserRole role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        return userRepository.save(new AppUser(username, role));
    }

    public AppUser findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }
}
