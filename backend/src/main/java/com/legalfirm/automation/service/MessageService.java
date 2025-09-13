package com.legalfirm.automation.service;

import com.legalfirm.automation.dto.request.MessageRequest;
import com.legalfirm.automation.dto.response.MessageResponse;
import com.legalfirm.automation.dto.response.PageResponse;
import com.legalfirm.automation.dto.response.UserResponse;
import com.legalfirm.automation.entity.Message;
import com.legalfirm.automation.entity.User;
import com.legalfirm.automation.exception.BadRequestException;
import com.legalfirm.automation.exception.ResourceNotFoundException;
import com.legalfirm.automation.repository.MessageRepository;
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
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public PageResponse<MessageResponse> getUserMessages(UUID userId, Pageable pageable) {
        Page<Message> messagePage = messageRepository.findUserMessages(userId, pageable);
        return mapToPageResponse(messagePage);
    }

    public List<MessageResponse> getConversation(UUID userId1, UUID userId2) {
        return messageRepository.findConversation(userId1, userId2).stream()
                .map(this::mapToMessageResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageResponse sendMessage(MessageRequest request, UUID senderId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found with id: " + senderId));
        
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found with id: " + request.getReceiverId()));
        
        if (senderId.equals(request.getReceiverId())) {
            throw new BadRequestException("Cannot send message to yourself");
        }
        
        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .isRead(false)
                .build();
        
        Message savedMessage = messageRepository.save(message);
        return mapToMessageResponse(savedMessage);
    }

    @Transactional
    public void markMessageAsRead(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
        
        message.setIsRead(true);
        messageRepository.save(message);
    }

    @Transactional
    public void deleteMessage(UUID id) {
        if (!messageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Message not found with id: " + id);
        }
        messageRepository.deleteById(id);
    }

    public long getUnreadMessagesCount(UUID userId) {
        return messageRepository.countUnreadMessages(userId);
    }

    private MessageResponse mapToMessageResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .sender(mapToUserResponse(message.getSender()))
                .receiver(mapToUserResponse(message.getReceiver()))
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .isRead(message.getIsRead())
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

    private PageResponse<MessageResponse> mapToPageResponse(Page<Message> page) {
        return PageResponse.<MessageResponse>builder()
                .content(page.getContent().stream().map(this::mapToMessageResponse).collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}