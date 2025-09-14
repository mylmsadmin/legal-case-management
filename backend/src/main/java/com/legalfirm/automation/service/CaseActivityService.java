package com.legalfirm.automation.service;

import com.legalfirm.automation.dto.response.CaseActivityResponse;
import com.legalfirm.automation.dto.response.PageResponse;
import com.legalfirm.automation.dto.response.UserResponse;
import com.legalfirm.automation.entity.Case;
import com.legalfirm.automation.entity.CaseActivity;
import com.legalfirm.automation.entity.User;
import com.legalfirm.automation.exception.ResourceNotFoundException;
import com.legalfirm.automation.repository.CaseActivityRepository;
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
@Transactional(readOnly = true)
public class CaseActivityService {
    
    private final CaseActivityRepository caseActivityRepository;

    @Transactional
    public void logActivity(Case caseEntity, User performedBy, String activityType, 
                           String description, String oldValue, String newValue) {
        CaseActivity activity = CaseActivity.builder()
                .caseEntity(caseEntity)
                .performedBy(performedBy)
                .activityType(activityType)
                .description(description)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();
        
        caseActivityRepository.save(activity);
    }

    public List<CaseActivityResponse> getCaseActivities(UUID caseId) {
        List<CaseActivity> activities = caseActivityRepository.findByCaseEntityIdOrderByActivityDateDesc(caseId);
        return activities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PageResponse<CaseActivityResponse> getCaseActivitiesPaginated(UUID caseId, Pageable pageable) {
        Page<CaseActivity> activityPage = caseActivityRepository.findByCaseEntityId(caseId, pageable);
        return mapToPageResponse(activityPage);
    }

    public List<CaseActivityResponse> getCaseActivitiesByDateRange(UUID caseId, 
                                                                   LocalDateTime startDate, 
                                                                   LocalDateTime endDate) {
        List<CaseActivity> activities = caseActivityRepository.findByCaseIdAndDateRange(caseId, startDate, endDate);
        return activities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PageResponse<CaseActivityResponse> getUserActivities(UUID userId, Pageable pageable) {
        Page<CaseActivity> activityPage = caseActivityRepository.findByPerformedBy(userId, pageable);
        return mapToPageResponse(activityPage);
    }

    private CaseActivityResponse mapToResponse(CaseActivity activity) {
        return CaseActivityResponse.builder()
                .id(activity.getId())
                .activityType(activity.getActivityType())
                .description(activity.getDescription())
                .oldValue(activity.getOldValue())
                .newValue(activity.getNewValue())
                .caseId(activity.getCaseEntity().getId())
                .caseTitle(activity.getCaseEntity().getTitle())
                .performedBy(mapToUserResponse(activity.getPerformedBy()))
                .activityDate(activity.getActivityDate())
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    private PageResponse<CaseActivityResponse> mapToPageResponse(Page<CaseActivity> page) {
        return PageResponse.<CaseActivityResponse>builder()
                .content(page.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}