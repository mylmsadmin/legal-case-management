package com.legalfirm.automation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private UUID id;
    private UserResponse sender;
    private UserResponse receiver;
    private String content;
    private LocalDateTime timestamp;
    private Boolean isRead;
}