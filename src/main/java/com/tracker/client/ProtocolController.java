package com.tracker.client;

import com.tracker.domain.Protocol;
import com.tracker.dto.ProtocolRequest;
import com.tracker.manager.ProtocolManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/protocols")
public class ProtocolController {

    private final ProtocolManager protocolManager;

    public ProtocolController(ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
    }

    @GetMapping
    public List<Protocol> listAll() {
        return protocolManager.listAll();
    }

    @GetMapping("/{id}")
    public Protocol getById(@PathVariable Long id) {
        return protocolManager.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Protocol create(@RequestBody ProtocolRequest request) {
        return protocolManager.create(request);
    }
}
