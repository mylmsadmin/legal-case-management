package com.legalfirm.automation.service;

import com.legalfirm.automation.dto.request.ClientRequest;
import com.legalfirm.automation.dto.response.ClientResponse;
import com.legalfirm.automation.dto.response.PageResponse;
import com.legalfirm.automation.entity.Client;
import com.legalfirm.automation.exception.ResourceNotFoundException;
import com.legalfirm.automation.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {
    
    private final ClientRepository clientRepository;

    public PageResponse<ClientResponse> getAllClients(Pageable pageable) {
        Page<Client> clientPage = clientRepository.findAll(pageable);
        return mapToPageResponse(clientPage);
    }

    public PageResponse<ClientResponse> searchClients(String search, Pageable pageable) {
        Page<Client> clientPage = clientRepository.searchClients(search, pageable);
        return mapToPageResponse(clientPage);
    }

    public ClientResponse getClientById(UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        return mapToClientResponse(client);
    }

    @Transactional
    public ClientResponse createClient(ClientRequest request) {
        Client client = Client.builder()
                .name(request.getName())
                .contactInfo(request.getContactInfo())
                .address(request.getAddress())
                .build();
        
        Client savedClient = clientRepository.save(client);
        return mapToClientResponse(savedClient);
    }

    @Transactional
    public ClientResponse updateClient(UUID id, ClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        
        client.setName(request.getName());
        client.setContactInfo(request.getContactInfo());
        client.setAddress(request.getAddress());
        
        Client updatedClient = clientRepository.save(client);
        return mapToClientResponse(updatedClient);
    }

    @Transactional
    public void deleteClient(UUID id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client not found with id: " + id);
        }
        clientRepository.deleteById(id);
    }

    private ClientResponse mapToClientResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .name(client.getName())
                .contactInfo(client.getContactInfo())
                .address(client.getAddress())
                .totalCases(client.getCases() != null ? client.getCases().size() : 0)
                .build();
    }

    private PageResponse<ClientResponse> mapToPageResponse(Page<Client> page) {
        return PageResponse.<ClientResponse>builder()
                .content(page.getContent().stream().map(this::mapToClientResponse).collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}