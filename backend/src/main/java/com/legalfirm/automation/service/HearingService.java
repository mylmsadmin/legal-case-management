package com.legalfirm.automation.service;

import com.legalfirm.automation.dto.request.HearingRequest;
import com.legalfirm.automation.dto.response.HearingResponse;
import com.legalfirm.automation.dto.response.PageResponse;
import com.legalfirm.automation.entity.Case;
import com.legalfirm.automation.entity.Hearing;
import com.legalfirm.automation.exception.ResourceNotFoundException;
import com.legalfirm.automation.repository.CaseRepository;
import com.legalfirm.automation.repository.HearingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HearingService {
    
    private final HearingRepository hearingRepository;
    private final CaseRepository caseRepository;

    public List<HearingResponse> getAllHearings() {
        return hearingRepository.findAll().stream()
                .map(this::mapToHearingResponse)
                .collect(Collectors.toList());
    }

    public PageResponse<HearingResponse> getUpcomingHearings(Pageable pageable) {
        Page<Hearing> hearingPage = hearingRepository.findUpcomingHearings(pageable);
        return mapToPageResponse(hearingPage);
    }

    public List<HearingResponse> getHearingsByCase(UUID caseId) {
        return hearingRepository.findByCaseEntityId(caseId).stream()
                .map(this::mapToHearingResponse)
                .collect(Collectors.toList());
    }

    public List<HearingResponse> getHearingsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return hearingRepository.findHearingsBetweenDates(startDate, endDate).stream()
                .map(this::mapToHearingResponse)
                .collect(Collectors.toList());
    }

    public HearingResponse getHearingById(UUID id) {
        Hearing hearing = hearingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hearing not found with id: " + id));
        return mapToHearingResponse(hearing);
    }

    @Transactional
    public HearingResponse createHearing(HearingRequest request) {
        Case caseEntity = caseRepository.findById(request.getCaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + request.getCaseId()));
        
        Hearing hearing = Hearing.builder()
                .date(request.getDate())
                .notes(request.getNotes())
                .caseEntity(caseEntity)
                .build();
        
        Hearing savedHearing = hearingRepository.save(hearing);
        return mapToHearingResponse(savedHearing);
    }

    @Transactional
    public HearingResponse updateHearing(UUID id, HearingRequest request) {
        Hearing hearing = hearingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hearing not found with id: " + id));
        
        hearing.setDate(request.getDate());
        hearing.setNotes(request.getNotes());
        
        Hearing updatedHearing = hearingRepository.save(hearing);
        return mapToHearingResponse(updatedHearing);
    }

    @Transactional
    public void deleteHearing(UUID id) {
        if (!hearingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Hearing not found with id: " + id);
        }
        hearingRepository.deleteById(id);
    }

    private HearingResponse mapToHearingResponse(Hearing hearing) {
        return HearingResponse.builder()
                .id(hearing.getId())
                .date(hearing.getDate())
                .notes(hearing.getNotes())
                .caseId(hearing.getCaseEntity().getId())
                .caseTitle(hearing.getCaseEntity().getTitle())
                .clientName(hearing.getCaseEntity().getClient().getName())
                .createdAt(hearing.getCreatedAt())
                .build();
    }

    private PageResponse<HearingResponse> mapToPageResponse(Page<Hearing> page) {
        return PageResponse.<HearingResponse>builder()
                .content(page.getContent().stream().map(this::mapToHearingResponse).collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}