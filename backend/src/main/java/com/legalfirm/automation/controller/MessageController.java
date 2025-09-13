package com.legalfirm.automation.controller;

import com.legalfirm.automation.dto.request.MessageRequest;
import com.legalfirm.automation.dto.response.MessageResponse;
import com.legalfirm.automation.dto.response.PageResponse;
import com.legalfirm.automation.entity.User;
import com.legalfirm.automation.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<PageResponse<MessageResponse>> getUserMessages(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return ResponseEntity.ok(messageService.getUserMessages(user.getId(), pageable));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<MessageResponse>> getConversation(
            @AuthenticationPrincipal User user,
            @PathVariable UUID userId) {
        return ResponseEntity.ok(messageService.getConversation(user.getId(), userId));
    }

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @Valid @RequestBody MessageRequest request,
            @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(
                messageService.sendMessage(request, user.getId()),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markMessageAsRead(@PathVariable UUID id) {
        messageService.markMessageAsRead(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal User user) {
        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", messageService.getUnreadMessagesCount(user.getId()));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}