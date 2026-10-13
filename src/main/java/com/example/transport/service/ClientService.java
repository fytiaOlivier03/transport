package com.example.transport.service;

import com.example.transport.dto.request.ClientRequest;
import com.example.transport.dto.response.ClientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClientService {
    ClientResponse creer(ClientRequest request);

    ClientResponse modifier(Long id, ClientRequest request);

    ClientResponse trouver(Long id);

    ClientRequest getRequest(Long id);

    Page<ClientResponse> rechercher(String q, Pageable pageable);

    List<ClientResponse> tous();

    void supprimer(Long id);
}
