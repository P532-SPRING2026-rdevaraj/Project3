package com.tracker.manager;

import com.tracker.domain.Protocol;
import com.tracker.dto.ProtocolRequest;
import com.tracker.resourceaccess.ProtocolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProtocolManager {

    private final ProtocolRepository protocolRepository;

    public ProtocolManager(ProtocolRepository protocolRepository) {
        this.protocolRepository = protocolRepository;
    }

    public List<Protocol> listAll() {
        return protocolRepository.findAll();
    }

    public Protocol findById(Long id) {
        return protocolRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Protocol not found: " + id));
    }

    public Protocol create(ProtocolRequest request) {
        Protocol p = new Protocol(request.getName(), request.getDescription(), request.getAccuracyRating());
        return protocolRepository.save(p);
    }
}
