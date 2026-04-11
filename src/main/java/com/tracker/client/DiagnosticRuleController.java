package com.tracker.client;

import com.tracker.domain.AssociativeFunction;
import com.tracker.dto.AssociativeFunctionRequest;
import com.tracker.manager.DiagnosticRuleManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Client layer — HTTP only, zero business logic.
 * Manages AssociativeFunction (diagnostic rule) CRUD.
 */
@RestController
@RequestMapping("/api/rules")
public class DiagnosticRuleController {

    private final DiagnosticRuleManager diagnosticRuleManager;

    public DiagnosticRuleController(DiagnosticRuleManager diagnosticRuleManager) {
        this.diagnosticRuleManager = diagnosticRuleManager;
    }

    /** GET /api/rules — List all rules. */
    @GetMapping
    public List<AssociativeFunction> listAll() {
        return diagnosticRuleManager.listAll();
    }

    /** POST /api/rules — Create a diagnostic rule. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssociativeFunction create(@RequestBody AssociativeFunctionRequest request) {
        return diagnosticRuleManager.create(request);
    }
}
