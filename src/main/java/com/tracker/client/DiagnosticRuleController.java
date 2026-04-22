package com.tracker.client;

import com.tracker.domain.AssociativeFunction;
import com.tracker.dto.AssociativeFunctionRequest;
import com.tracker.manager.DiagnosticRuleManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
public class DiagnosticRuleController {

    private final DiagnosticRuleManager diagnosticRuleManager;

    public DiagnosticRuleController(DiagnosticRuleManager diagnosticRuleManager) {
        this.diagnosticRuleManager = diagnosticRuleManager;
    }

    @GetMapping
    public List<AssociativeFunction> listAll() {
        return diagnosticRuleManager.listAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssociativeFunction create(@RequestBody AssociativeFunctionRequest request) {
        return diagnosticRuleManager.create(request);
    }
}
