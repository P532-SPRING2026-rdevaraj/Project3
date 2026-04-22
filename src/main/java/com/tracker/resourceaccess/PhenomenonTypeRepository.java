package com.tracker.resourceaccess;

import com.tracker.domain.PhenomenonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhenomenonTypeRepository extends JpaRepository<PhenomenonType, Long> {
}
