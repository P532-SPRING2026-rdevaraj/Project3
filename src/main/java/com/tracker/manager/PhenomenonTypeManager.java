package com.tracker.manager;

import com.tracker.domain.MeasurementKind;
import com.tracker.domain.Phenomenon;
import com.tracker.domain.PhenomenonType;
import com.tracker.dto.PhenomenonRequest;
import com.tracker.dto.PhenomenonTypeRequest;
import com.tracker.resourceaccess.PhenomenonRepository;
import com.tracker.resourceaccess.PhenomenonTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Manager layer — orchestrates phenomenon-type and phenomenon use-cases (F2).
 *
 * Knowledge-level entities (PhenomenonType, Phenomenon) are managed here.
 * They are never created as side-effects of recording observations.
 */
@Service
public class PhenomenonTypeManager {

    private final PhenomenonTypeRepository phenomenonTypeRepository;
    private final PhenomenonRepository phenomenonRepository;

    public PhenomenonTypeManager(PhenomenonTypeRepository phenomenonTypeRepository,
                                 PhenomenonRepository phenomenonRepository) {
        this.phenomenonTypeRepository = phenomenonTypeRepository;
        this.phenomenonRepository = phenomenonRepository;
    }

    public List<PhenomenonType> listAll() {
        return phenomenonTypeRepository.findAll();
    }

    public PhenomenonType findById(Long id) {
        return phenomenonTypeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("PhenomenonType not found: " + id));
    }

    public PhenomenonType create(PhenomenonTypeRequest request) {
        PhenomenonType pt = new PhenomenonType(request.getName(), request.getKind());
        if (request.getKind() == MeasurementKind.QUANTITATIVE && request.getAllowedUnits() != null) {
            pt.setAllowedUnits(request.getAllowedUnits());
        }
        return phenomenonTypeRepository.save(pt);
    }

    public Phenomenon addPhenomenon(PhenomenonRequest request) {
        PhenomenonType pt = findById(request.getPhenomenonTypeId());
        if (pt.getKind() != MeasurementKind.QUALITATIVE) {
            throw new IllegalArgumentException(
                "Can only add phenomena to QUALITATIVE phenomenon types");
        }
        Phenomenon ph = new Phenomenon(request.getName(), pt);
        return phenomenonRepository.save(ph);
    }

    public Phenomenon findPhenomenonById(Long id) {
        return phenomenonRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Phenomenon not found: " + id));
    }
}
