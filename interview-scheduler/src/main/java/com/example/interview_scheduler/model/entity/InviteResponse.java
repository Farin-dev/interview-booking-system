package com.example.interview_scheduler.model.entity;

import com.example.interview_scheduler.model.enums.ResponseStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "invite_responses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InviteResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Recipient email is required")
    @Email(message = "Recipient email must be a valid email address")
    private String recipientEmail;

    @NotNull(message = "Response status is required")
    @Enumerated(EnumType.STRING)
    private ResponseStatus responseStatus;

    private LocalDateTime proposedDateTime;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
}
