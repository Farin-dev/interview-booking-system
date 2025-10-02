package com.example.interview_scheduler.model.dto;

import com.example.interview_scheduler.model.enums.MeetingPlatform;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BookingRequestDTO(
        @NotBlank
        String candidateName,
        @NotBlank
        String interviewerName,
        @NotNull @Future
        LocalDateTime proposedDateTime,
        @NotNull
        MeetingPlatform platform,
        @NotBlank
        String recipientEmail
){}