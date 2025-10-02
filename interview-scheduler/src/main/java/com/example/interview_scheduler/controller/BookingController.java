package com.example.interview_scheduler.controller;

import com.example.interview_scheduler.exception.BookingException;
import com.example.interview_scheduler.model.dto.BookingRequestDTO;
import com.example.interview_scheduler.model.dto.BookingResponseDTO;
import com.example.interview_scheduler.model.dto.InviteResponseDTO;
import com.example.interview_scheduler.response.BaseResponse;
import com.example.interview_scheduler.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking Management", description = "APIs for scheduling, responding, and checking interview booking status")

public class BookingController {

    private final BookingService bookingService;


    @PostMapping
    @Operation(
            summary = "Schedule a new interview booking",
            description = "Creates a new interview booking and sends an invite to the recipient."
    )
    public ResponseEntity<BaseResponse<BookingResponseDTO>> createBooking(
            @Valid @RequestBody BookingRequestDTO dto) {
        log.info("Request received to create booking for candidate={} interviewer={}", dto.candidateName(), dto.interviewerName());
        try {
            BookingResponseDTO response = bookingService.createBooking(dto);
            log.info("Booking created successfully with id={}", response.id());
            return ResponseEntity.ok(BaseResponse.success(
                    "Booking created successfully", response, 200));
        } catch (BookingException e) {
            log.warn("Failed to create booking: {}", e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage(), 400));
        } catch (Exception e) {
            log.error("Unexpected error while creating booking", e);
            return ResponseEntity.status(500).body(BaseResponse.failure("Internal server error", 500));
        }
    }

    @PostMapping("/{id}/respond")
    @Operation(
            summary = "Respond to a meeting invite",
            description = "Allows the recipient to accept, reject, or propose a new time for the meeting invite."
    )
    public ResponseEntity<BaseResponse<BookingResponseDTO>> respondToInvite(
            @PathVariable Long id,
            @Valid @RequestBody InviteResponseDTO dto) {
        log.info("Request received to respond to booking id={} with status={}", id, dto.responseStatus());
        try {
            BookingResponseDTO response = bookingService.respondToInvite(id, dto);
            log.info("Invite response recorded for booking id={}", id);
            return ResponseEntity.ok(BaseResponse.success(
                    "Invite response recorded successfully", response, 200));
        } catch (BookingException e) {
            log.warn("Failed to respond to invite for booking id={}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage(), 400));
        } catch (Exception e) {
            log.error("Unexpected error while responding to invite for booking id={}", id, e);
            return ResponseEntity.status(500).body(BaseResponse.failure("Internal server error", 500));
        }
    }

    @GetMapping("/status/{id}")
    @Operation(
            summary = "Get interview booking status",
            description = "Fetches the current status of a booking by its ID."
    )
    public ResponseEntity<BaseResponse<BookingResponseDTO>> getBookingStatus(
            @PathVariable Long id) {
        log.info("Fetching booking status for id={}", id);
        try {
            BookingResponseDTO response = bookingService.getBookingStatus(id);
            log.info("Booking status fetched for id={} with status={}", id, response.status());
            return ResponseEntity.ok(BaseResponse.success(
                    "Booking status fetched successfully", response, 200));
        } catch (BookingException e) {
            log.warn("Booking not found for id={}", id);
            return ResponseEntity.status(404).body(BaseResponse.failure(e.getMessage(), 404));
        } catch (Exception e) {
            log.error("Unexpected error while fetching booking status for id={}", id, e);
            return ResponseEntity.status(500).body(BaseResponse.failure("Internal server error", 500));
        }
    }
}
