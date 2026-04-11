package com.tracker.resourceaccess;

import com.tracker.domain.PhenomenonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ResourceAccess layer — atomic business verbs for PhenomenonType persistence.
 */
@Repository
public interface PhenomenonTypeRepository extends JpaRepository<PhenomenonType, Long> {
}
