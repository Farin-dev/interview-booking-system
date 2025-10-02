package com.example.interview_scheduler.model.dto;

import com.example.interview_scheduler.model.enums.ResponseStatus;

import java.time.LocalDateTime;

public record InviteResponseDTO(
        String recipientEmail,
        ResponseStatus responseStatus,
        LocalDateTime proposedDateTime
) { }