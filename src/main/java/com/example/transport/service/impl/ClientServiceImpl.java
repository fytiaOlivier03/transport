package com.example.transport.service.impl;

import com.example.transport.dto.request.ClientRequest;
import com.example.transport.dto.response.ClientResponse;
import com.example.transport.entity.Client;
import com.example.transport.exception.ConflictException;
import com.example.transport.exception.ResourceNotFoundException;
import com.example.transport.mapper.ClientMapper;
import com.example.transport.repository.ClientRepository;
import com.example.transport.repository.MissionRepository;
import com.example.transport.service.ClientService;
import com.example.transport.util.RechercheUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@SuppressWarnings("all")
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final MissionRepository missionRepository;
    private final ClientMapper mapper;

    @Override
    @Transactional
    public ClientResponse creer(ClientRequest request) {
        Client c = new Client();
        mapper.updateEntity(request, c);
        return mapper.toResponse(clientRepository.save(c));
    }

    @Override
    @Transactional
    public ClientResponse modifier(Long id, ClientRequest request) {
        Client c = chercher(id);
        mapper.updateEntity(request, c);
        return mapper.toResponse(clientRepository.save(c));
    }

    @Override
    public ClientResponse trouver(Long id) {
        return mapper.toResponse(chercher(id));
    }

    @Override
    public ClientRequest getRequest(Long id) {
        return mapper.toRequest(chercher(id));
    }

    @Override
    public Page<ClientResponse> rechercher(String q, Pageable pageable) {
        Specification<Client> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (q != null && !q.isBlank()) {
                p.add(cb.or(
                        RechercheUtil.like(cb, root.get("nom"), q),
                        RechercheUtil.like(cb, root.get("prenom"), q),
                        RechercheUtil.like(cb, root.get("telephone"), q),
                        RechercheUtil.like(cb, root.get("email"), q)));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };
        return clientRepository.findAll(spec, pageable).map(c -> mapper.toResponse(c));
    }

    @Override
    public List<ClientResponse> tous() {
        return clientRepository.findAll(Sort.by("nom")).stream().map(c -> mapper.toResponse(c)).toList();
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        Client c = chercher(id);
        if (missionRepository.existsByClientId(id)) {
            throw new ConflictException("Impossible de supprimer ce client : des missions lui sont associées");
        }
        clientRepository.delete(c);
    }

    private Client chercher(Long id) {
        return clientRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Client", id));
    }
}
