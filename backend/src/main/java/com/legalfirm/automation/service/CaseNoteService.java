package com.legalfirm.automation.service;

import com.legalfirm.automation.dto.request.CaseNoteRequest;
import com.legalfirm.automation.dto.response.CaseNoteResponse;
import com.legalfirm.automation.dto.response.PageResponse;
import com.legalfirm.automation.dto.response.UserResponse;
import com.legalfirm.automation.entity.Case;
import com.legalfirm.automation.entity.CaseNote;
import com.legalfirm.automation.entity.User;
import com.legalfirm.automation.exception.ResourceNotFoundException;
import com.legalfirm.automation.exception.UnauthorizedException;
import com.legalfirm.automation.repository.CaseNoteRepository;
import com.legalfirm.automation.repository.CaseRepository;
import com.legalfirm.automation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaseNoteService {
    
    private final CaseNoteRepository caseNoteRepository;
    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final CaseActivityService caseActivityService;

    @Transactional
    public CaseNoteResponse addNote(CaseNoteRequest request, UUID userId) {
        Case caseEntity = caseRepository.findById(request.getCaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + request.getCaseId()));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        CaseNote note = CaseNote.builder()
                .content(request.getContent())
                .caseEntity(caseEntity)
                .createdBy(user)
                .isPrivate(request.getIsPrivate())
                .build();
        
        CaseNote savedNote = caseNoteRepository.save(note);
        
        // Log activity
        caseActivityService.logActivity(
                caseEntity,
                user,
                "NOTE_ADDED",
                "Added " + (request.getIsPrivate() ? "private" : "public") + " note to case",
                null,
                null
        );
        
        return mapToResponse(savedNote);
    }

    public List<CaseNoteResponse> getCaseNotes(UUID caseId, UUID userId) {
        // Check if case exists
        if (!caseRepository.existsById(caseId)) {
            throw new ResourceNotFoundException("Case not found with id: " + caseId);
        }
        
        List<CaseNote> notes = caseNoteRepository.findVisibleNotes(caseId, userId);
        return notes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PageResponse<CaseNoteResponse> getCaseNotesPaginated(UUID caseId, Pageable pageable) {
        Page<CaseNote> notePage = caseNoteRepository.findByCaseEntityId(caseId, pageable);
        return mapToPageResponse(notePage);
    }

    @Transactional
    public CaseNoteResponse updateNote(UUID noteId, String content, UUID userId) {
        CaseNote note = caseNoteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + noteId));
        
        // Check if user is the creator
        if (!note.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("You can only edit your own notes");
        }
        
        note.setContent(content);
        CaseNote updatedNote = caseNoteRepository.save(note);
        
        return mapToResponse(updatedNote);
    }

    @Transactional
    public void deleteNote(UUID noteId, UUID userId) {
        CaseNote note = caseNoteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + noteId));
        
        // Check if user is the creator or admin
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!note.getCreatedBy().getId().equals(userId) && !user.getRole().equals("ADMIN")) {
            throw new UnauthorizedException("You can only delete your own notes");
        }
        
        caseNoteRepository.deleteById(noteId);
    }

    private CaseNoteResponse mapToResponse(CaseNote note) {
        return CaseNoteResponse.builder()
                .id(note.getId())
                .content(note.getContent())
                .isPrivate(note.getIsPrivate())
                .createdBy(mapToUserResponse(note.getCreatedBy()))
                .caseId(note.getCaseEntity().getId())
                .caseTitle(note.getCaseEntity().getTitle())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
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

    private PageResponse<CaseNoteResponse> mapToPageResponse(Page<CaseNote> page) {
        return PageResponse.<CaseNoteResponse>builder()
                .content(page.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}