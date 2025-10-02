package com.example.interview_scheduler.model.entity;

import com.example.interview_scheduler.model.enums.BookingStatus;
import com.example.interview_scheduler.model.enums.MeetingPlatform;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Candidate name is required")
    private String candidateName;

    @NotBlank(message = "Interviewer name is required")
    private String interviewerName;

    @NotNull(message = "Proposed date and time is required")
    private LocalDateTime proposedDateTime;

    @NotNull(message = "Meeting platform is required")
    @Enumerated(EnumType.STRING)
    private MeetingPlatform platform;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<InviteResponse> responses = new ArrayList<>();
}