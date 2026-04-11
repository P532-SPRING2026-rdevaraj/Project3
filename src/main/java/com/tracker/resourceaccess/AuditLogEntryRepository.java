package com.tracker.resourceaccess;

import com.tracker.domain.AuditLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ResourceAccess layer — persistence for Observer-pattern audit log entries.
 */
@Repository
public interface AuditLogEntryRepository extends JpaRepository<AuditLogEntry, Long> {

    List<AuditLogEntry> findAllByOrderByTimestampDesc();
}
