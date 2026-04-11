package com.tracker.resourceaccess;

import com.tracker.domain.CommandLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ResourceAccess layer — persistence for Command log entries.
 */
@Repository
public interface CommandLogEntryRepository extends JpaRepository<CommandLogEntry, Long> {

    List<CommandLogEntry> findAllByOrderByExecutedAtDesc();
}
