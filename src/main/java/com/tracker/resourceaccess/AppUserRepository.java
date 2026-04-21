package com.tracker.resourceaccess;

import com.tracker.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** ResourceAccess layer — atomic verbs for AppUser persistence (Change 3). */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}
