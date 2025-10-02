package com.example.interview_scheduler.service;

import com.example.interview_scheduler.exception.BookingException;
import com.example.interview_scheduler.model.dto.BookingRequestDTO;
import com.example.interview_scheduler.model.dto.BookingResponseDTO;
import com.example.interview_scheduler.model.dto.InviteResponseDTO;
import com.example.interview_scheduler.model.entity.Booking;
import com.example.interview_scheduler.model.entity.InviteResponse;
import com.example.interview_scheduler.model.enums.BookingStatus;
import com.example.interview_scheduler.model.enums.ResponseStatus;
import com.example.interview_scheduler.repository.BookingRepository;
import com.example.interview_scheduler.repository.InviteResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final InviteResponseRepository inviteResponseRepository;
    private final EmailService emailService;

    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO dto) {


        boolean conflict = bookingRepository.existsByProposedDateTime(dto.proposedDateTime());
        if (conflict) {
            log.warn("Time slot already booked at {}", dto.proposedDateTime());
            throw new BookingException("Time slot is already booked. Please choose a different time.");
        }

        Booking booking = Booking.builder()
                .candidateName(dto.candidateName())
                .interviewerName(dto.interviewerName())
                .proposedDateTime(dto.proposedDateTime())
                .platform(dto.platform())
                .status(BookingStatus.PENDING)
                .build();

        booking = bookingRepository.save(booking);
        log.info("Booking saved with id={}", booking.getId());

        InviteResponse invite = InviteResponse.builder()
                .recipientEmail(dto.recipientEmail())
                .responseStatus(ResponseStatus.PENDING)
                .booking(booking)
                .build();
        inviteResponseRepository.save(invite);
        log.info("Invite created for bookingId={} email={}", booking.getId(), dto.recipientEmail());

        emailService.sendInvite(
                dto.recipientEmail(),
                "Interview Invitation",
                String.format("Dear %s,\nYour interview with %s is scheduled at %s on %s.",
                        dto.candidateName(),
                        dto.interviewerName(),
                        dto.proposedDateTime(),
                        dto.platform())
        );
        log.info("Email invite sent to {}", dto.recipientEmail());

        return mapToResponseDTO(booking, invite);
    }


    @Transactional
    public BookingResponseDTO respondToInvite(Long bookingId, InviteResponseDTO dto) {
        log.info("Fetching booking for id={}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingException("Booking not found"));

        InviteResponse response = inviteResponseRepository.findByBookingId(bookingId);
        if (response == null) {
            log.warn("Invite not found for bookingId={}", bookingId);
            throw new BookingException("Invite not found");
        }

        response.setResponseStatus(dto.responseStatus());
        response.setProposedDateTime(dto.proposedDateTime());
        inviteResponseRepository.save(response);
        log.info("Invite updated for bookingId={} with response={}", bookingId, dto.responseStatus());

        switch (dto.responseStatus()) {
            case ACCEPTED -> booking.setStatus(BookingStatus.ACCEPTED);
            case REJECTED -> booking.setStatus(BookingStatus.REJECTED);
            case PROPOSED -> booking.setStatus(BookingStatus.RESCHEDULED);
            default -> booking.setStatus(BookingStatus.PENDING);
        }
        bookingRepository.save(booking);
        log.info("Booking status updated for id={} newStatus={}", bookingId, booking.getStatus());

        return mapToResponseDTO(booking, response);
    }

    public BookingResponseDTO getBookingStatus(Long bookingId) {
        log.info("Fetching booking status for id={}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingException("Booking not found"));

        InviteResponse response = inviteResponseRepository.findByBookingId(bookingId);
        if (response == null) {
            log.warn("Invite not found for bookingId={}", bookingId);
            throw new BookingException("Invite not found");
        }

        log.info("Booking status for id={} is {}", bookingId, booking.getStatus());
        return mapToResponseDTO(booking, response);
    }

    private BookingResponseDTO mapToResponseDTO(Booking booking, InviteResponse invite) {
        InviteResponseDTO responseDTO = new InviteResponseDTO(
                invite.getRecipientEmail(),
                invite.getResponseStatus(),
                invite.getProposedDateTime()
        );

        return new BookingResponseDTO(
                booking.getId(),
                booking.getCandidateName(),
                booking.getInterviewerName(),
                booking.getProposedDateTime(),
                booking.getPlatform(),
                booking.getStatus(),
                List.of(responseDTO)
        );
    }
}
