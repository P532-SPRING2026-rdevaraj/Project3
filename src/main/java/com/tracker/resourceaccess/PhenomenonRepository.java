package com.tracker.resourceaccess;

import com.tracker.domain.Phenomenon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ResourceAccess layer — atomic business verbs for Phenomenon persistence.
 */
@Repository
public interface PhenomenonRepository extends JpaRepository<Phenomenon, Long> {

    List<Phenomenon> findByPhenomenonTypeId(Long phenomenonTypeId);
}
