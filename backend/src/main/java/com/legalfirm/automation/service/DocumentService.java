package com.legalfirm.automation.service;

import com.legalfirm.automation.dto.response.DocumentResponse;
import com.legalfirm.automation.dto.response.PageResponse;
import com.legalfirm.automation.dto.response.UserResponse;
import com.legalfirm.automation.entity.Case;
import com.legalfirm.automation.entity.Document;
import com.legalfirm.automation.entity.User;
import com.legalfirm.automation.exception.BadRequestException;
import com.legalfirm.automation.exception.ResourceNotFoundException;
import com.legalfirm.automation.repository.CaseRepository;
import com.legalfirm.automation.repository.DocumentRepository;
import com.legalfirm.automation.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {
    
    private final DocumentRepository documentRepository;
    private final CaseRepository caseRepository;
    private final FileStorageUtil fileStorageUtil;
    
    @Value("${application.file.allowed-extensions}")
    private String allowedExtensions;

    public PageResponse<DocumentResponse> getAllDocuments(Pageable pageable) {
        Page<Document> documentPage = documentRepository.findAll(pageable);
        return mapToPageResponse(documentPage);
    }

    public List<DocumentResponse> getDocumentsByCase(UUID caseId) {
        return documentRepository.findByCaseEntityId(caseId).stream()
                .map(this::mapToDocumentResponse)
                .collect(Collectors.toList());
    }

    public DocumentResponse getDocumentById(UUID id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
        return mapToDocumentResponse(document);
    }

    @Transactional
    public DocumentResponse uploadDocument(MultipartFile file, UUID caseId, UUID userId) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + caseId));
        
        validateFile(file);
        
        String filePath = fileStorageUtil.storeFile(file);
        
        Document document = Document.builder()
                .name(file.getOriginalFilename())
                .type(getFileExtension(file.getOriginalFilename()))
                .filePath(filePath)
                .fileSize(file.getSize())
                .caseEntity(caseEntity)
                .uploadedBy(User.builder().id(userId).build())
                .build();
        
        Document savedDocument = documentRepository.save(document);
        return mapToDocumentResponse(savedDocument);
    }

    public Resource downloadDocument(UUID id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
        
        return fileStorageUtil.loadFileAsResource(document.getFilePath());
    }

    @Transactional
    public void deleteDocument(UUID id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
        
        fileStorageUtil.deleteFile(document.getFilePath());
        documentRepository.deleteById(id);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }
        
        String fileExtension = getFileExtension(file.getOriginalFilename());
        List<String> allowedExtList = Arrays.asList(allowedExtensions.split(","));
        
        if (!allowedExtList.contains(fileExtension.toLowerCase())) {
            throw new BadRequestException("File type not allowed. Allowed types: " + allowedExtensions);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) return "";
        return fileName.substring(lastIndexOf + 1);
    }

    private DocumentResponse mapToDocumentResponse(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .name(document.getName())
                .type(document.getType())
                .fileSize(document.getFileSize())
                .uploadDate(document.getUploadDate())
                .caseId(document.getCaseEntity().getId())
                .caseTitle(document.getCaseEntity().getTitle())
                .uploadedBy(document.getUploadedBy() != null ? mapToUserResponse(document.getUploadedBy()) : null)
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

    private PageResponse<DocumentResponse> mapToPageResponse(Page<Document> page) {
        return PageResponse.<DocumentResponse>builder()
                .content(page.getContent().stream().map(this::mapToDocumentResponse).collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}