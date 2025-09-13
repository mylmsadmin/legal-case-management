package com.legalfirm.automation.service;

import com.legalfirm.automation.dto.request.CaseRequest;
import com.legalfirm.automation.dto.response.CaseResponse;
import com.legalfirm.automation.dto.response.ClientResponse;
import com.legalfirm.automation.dto.response.PageResponse;
import com.legalfirm.automation.dto.response.UserResponse;
import com.legalfirm.automation.entity.Case;
import com.legalfirm.automation.entity.Client;
import com.legalfirm.automation.entity.User;
import com.legalfirm.automation.enums.CaseStatus;
import com.legalfirm.automation.enums.Role;
import com.legalfirm.automation.exception.BadRequestException;
import com.legalfirm.automation.exception.ResourceNotFoundException;
import com.legalfirm.automation.repository.CaseRepository;
import com.legalfirm.automation.repository.ClientRepository;
import com.legalfirm.automation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaseService {

    private final CaseRepository caseRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public PageResponse<CaseResponse> getAllCases(Pageable pageable) {
        Page<Case> casePage = caseRepository.findAll(pageable);
        return mapToPageResponse(casePage);
    }

    public PageResponse<CaseResponse> searchCases(String search, CaseStatus status, Pageable pageable) {
        Page<Case> casePage = caseRepository.searchCases(search, status, pageable);
        return mapToPageResponse(casePage);
    }

    public PageResponse<CaseResponse> getCasesByStatus(CaseStatus status, Pageable pageable) {
        Page<Case> casePage = caseRepository.findByStatus(status, pageable);
        return mapToPageResponse(casePage);
    }

    public PageResponse<CaseResponse> getCasesByClient(UUID clientId, Pageable pageable) {
        // Validate client exists
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client not found with id: " + clientId);
        }
        Page<Case> casePage = caseRepository.findByClientId(clientId, pageable);
        return mapToPageResponse(casePage);
    }

    public PageResponse<CaseResponse> getCasesByLawyer(UUID lawyerId, Pageable pageable) {
        // Validate lawyer exists
        if (!userRepository.existsById(lawyerId)) {
            throw new ResourceNotFoundException("Lawyer not found with id: " + lawyerId);
        }
        Page<Case> casePage = caseRepository.findByAssignedLawyerId(lawyerId, pageable);
        return mapToPageResponse(casePage);
    }

    public CaseResponse getCaseById(UUID id) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + id));
        return mapToCaseResponse(caseEntity);
    }

    @Transactional
    public CaseResponse createCase(CaseRequest request) {
        validateCaseRequest(request);

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + request.getClientId()));

        User assignedLawyer = null;
        if (request.getAssignedLawyerId() != null) {
            assignedLawyer = userRepository.findById(request.getAssignedLawyerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lawyer not found with id: " + request.getAssignedLawyerId()));

            // Validate the user is actually a lawyer
            if (!Role.LAWYER.equals(assignedLawyer.getRole()) && !Role.ADMIN.equals(assignedLawyer.getRole())) {
                throw new BadRequestException("Assigned user must be a lawyer or admin");
            }
        }

        Case caseEntity = Case.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .client(client)
                .assignedLawyer(assignedLawyer)
                .build();

        Case savedCase = caseRepository.save(caseEntity);
        return mapToCaseResponse(savedCase);
    }

    @Transactional
    public CaseResponse updateCase(UUID id, CaseRequest request) {
        validateCaseRequest(request);

        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + id));

        if (request.getClientId() != null && !request.getClientId().equals(caseEntity.getClient().getId())) {
            Client client = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + request.getClientId()));
            caseEntity.setClient(client);
        }

        if (request.getAssignedLawyerId() != null) {
            User assignedLawyer = userRepository.findById(request.getAssignedLawyerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lawyer not found with id: " + request.getAssignedLawyerId()));

            // Validate the user is actually a lawyer
            if (!Role.LAWYER.equals(assignedLawyer.getRole()) && !Role.ADMIN.equals(assignedLawyer.getRole())) {
                throw new BadRequestException("Assigned user must be a lawyer or admin");
            }

            caseEntity.setAssignedLawyer(assignedLawyer);
        }

        caseEntity.setTitle(request.getTitle());
        caseEntity.setDescription(request.getDescription());
        caseEntity.setStatus(request.getStatus());

        Case updatedCase = caseRepository.save(caseEntity);
        return mapToCaseResponse(updatedCase);
    }

    @Transactional
    public void deleteCase(UUID id) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + id));

        // Add business logic validation
        if (!caseEntity.getHearings().isEmpty()) {
            throw new BadRequestException("Cannot delete case with scheduled hearings");
        }

        caseRepository.deleteById(id);
    }

    public long getCountByStatus(CaseStatus status) {
        return caseRepository.countByStatus(status);
    }

    private void validateCaseRequest(CaseRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Case title is required");
        }
        if (request.getClientId() == null) {
            throw new BadRequestException("Client ID is required");
        }
        if (request.getStatus() == null) {
            throw new BadRequestException("Case status is required");
        }
    }

    // Mapping methods remain the same...
    private CaseResponse mapToCaseResponse(Case caseEntity) {
        return CaseResponse.builder()
                .id(caseEntity.getId())
                .title(caseEntity.getTitle())
                .description(caseEntity.getDescription())
                .status(caseEntity.getStatus())
                .client(mapToClientResponse(caseEntity.getClient()))
                .assignedLawyer(caseEntity.getAssignedLawyer() != null ? mapToUserResponse(caseEntity.getAssignedLawyer()) : null)
                .totalHearings(caseEntity.getHearings() != null ? caseEntity.getHearings().size() : 0)
                .totalDocuments(caseEntity.getDocuments() != null ? caseEntity.getDocuments().size() : 0)
                .createdAt(caseEntity.getCreatedAt())
                .updatedAt(caseEntity.getUpdatedAt())
                .build();
    }

    private ClientResponse mapToClientResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .name(client.getName())
                .contactInfo(client.getContactInfo())
                .address(client.getAddress())
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .contactNumber(user.getContactNumber())
                .build();
    }

    private PageResponse<CaseResponse> mapToPageResponse(Page<Case> page) {
        return PageResponse.<CaseResponse>builder()
                .content(page.getContent().stream().map(this::mapToCaseResponse).collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}