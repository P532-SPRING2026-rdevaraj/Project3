package com.tracker.client;

import com.tracker.domain.Phenomenon;
import com.tracker.domain.PhenomenonType;
import com.tracker.dto.PhenomenonRequest;
import com.tracker.dto.PhenomenonTypeRequest;
import com.tracker.manager.PhenomenonTypeManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Client layer — HTTP only, zero business logic.
 * F2: Phenomenon-type catalogue.
 */
@RestController
@RequestMapping("/api/phenomenon-types")
public class PhenomenonTypeController {

    private final PhenomenonTypeManager phenomenonTypeManager;

    public PhenomenonTypeController(PhenomenonTypeManager phenomenonTypeManager) {
        this.phenomenonTypeManager = phenomenonTypeManager;
    }

    /** GET /api/phenomenon-types — List all phenomenon types. */
    @GetMapping
    public List<PhenomenonType> listAll() {
        return phenomenonTypeManager.listAll();
    }

    /** GET /api/phenomenon-types/{id} — Get single phenomenon type. */
    @GetMapping("/{id}")
    public PhenomenonType getById(@PathVariable Long id) {
        return phenomenonTypeManager.findById(id);
    }

    /** POST /api/phenomenon-types — Create phenomenon type. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PhenomenonType create(@RequestBody PhenomenonTypeRequest request) {
        return phenomenonTypeManager.create(request);
    }

    /** POST /api/phenomenon-types/phenomena — Add a phenomenon to a qualitative type. */
    @PostMapping("/phenomena")
    @ResponseStatus(HttpStatus.CREATED)
    public Phenomenon addPhenomenon(@RequestBody PhenomenonRequest request) {
        return phenomenonTypeManager.addPhenomenon(request);
    }
}
