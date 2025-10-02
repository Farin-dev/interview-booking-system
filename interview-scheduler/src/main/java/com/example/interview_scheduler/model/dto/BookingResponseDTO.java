package com.example.interview_scheduler.model.dto;

import com.example.interview_scheduler.model.enums.BookingStatus;
import com.example.interview_scheduler.model.enums.MeetingPlatform;

import java.time.LocalDateTime;
import java.util.List;

public record BookingResponseDTO(
        Long id,
        String candidateName,
        String interviewerName,
        LocalDateTime proposedDateTime,
        MeetingPlatform platform,
        BookingStatus status,
        List<InviteResponseDTO> responses
) { }