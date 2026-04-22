package com.tracker.client;

import com.tracker.domain.Phenomenon;
import com.tracker.domain.PhenomenonType;
import com.tracker.dto.PhenomenonRequest;
import com.tracker.dto.PhenomenonTypeRequest;
import com.tracker.manager.PhenomenonTypeManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phenomenon-types")
public class PhenomenonTypeController {

    private final PhenomenonTypeManager phenomenonTypeManager;

    public PhenomenonTypeController(PhenomenonTypeManager phenomenonTypeManager) {
        this.phenomenonTypeManager = phenomenonTypeManager;
    }

    @GetMapping
    public List<PhenomenonType> listAll() {
        return phenomenonTypeManager.listAll();
    }

    @GetMapping("/{id}")
    public PhenomenonType getById(@PathVariable Long id) {
        return phenomenonTypeManager.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PhenomenonType create(@RequestBody PhenomenonTypeRequest request) {
        return phenomenonTypeManager.create(request);
    }

    @PostMapping("/phenomena")
    @ResponseStatus(HttpStatus.CREATED)
    public Phenomenon addPhenomenon(@RequestBody PhenomenonRequest request) {
        return phenomenonTypeManager.addPhenomenon(request);
    }
}
