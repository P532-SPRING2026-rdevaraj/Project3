package com.tracker.client;

import com.tracker.domain.AppUser;
import com.tracker.domain.UserRole;
import com.tracker.engine.UserContextHolder;
import com.tracker.manager.UserManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Client layer — manages users and current-user session (Change 3).
 * No Spring Security; login just sets X-Current-User via the UI.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserManager userManager;

    public UserController(UserManager userManager) {
        this.userManager = userManager;
    }

    @GetMapping
    public List<AppUser> listUsers() {
        return userManager.listAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppUser createUser(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        UserRole role = UserRole.valueOf(body.getOrDefault("role", "CLINICIAN"));
        return userManager.create(username, role);
    }

    /** Returns the username currently active for this request (from X-Current-User header). */
    @GetMapping("/current")
    public Map<String, String> currentUser() {
        return Map.of("username", UserContextHolder.get());
    }
}
