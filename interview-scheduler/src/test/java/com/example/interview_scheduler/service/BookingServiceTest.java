package com.example.interview_scheduler.service;

import com.example.interview_scheduler.exception.BookingException;
import com.example.interview_scheduler.model.dto.BookingRequestDTO;
import com.example.interview_scheduler.model.dto.BookingResponseDTO;
import com.example.interview_scheduler.model.dto.InviteResponseDTO;
import com.example.interview_scheduler.model.entity.Booking;
import com.example.interview_scheduler.model.entity.InviteResponse;
import com.example.interview_scheduler.model.enums.BookingStatus;
import com.example.interview_scheduler.model.enums.MeetingPlatform;
import com.example.interview_scheduler.model.enums.ResponseStatus;
import com.example.interview_scheduler.repository.BookingRepository;
import com.example.interview_scheduler.repository.InviteResponseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private InviteResponseRepository inviteResponseRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void shouldCreateBookingSuccessfully() {
        BookingRequestDTO request = new BookingRequestDTO(
                "John",
                "Andy",
                LocalDateTime.now().plusDays(1),
                MeetingPlatform.GOOGLE,
                "John@gmail.com"
        );

        Booking booking = Booking.builder()
                .id(1L)
                .candidateName(request.candidateName())
                .interviewerName(request.interviewerName())
                .proposedDateTime(request.proposedDateTime())
                .platform(request.platform())
                .status(BookingStatus.PENDING)
                .build();

        InviteResponse invite = InviteResponse.builder()
                .id(1L)
                .recipientEmail(request.recipientEmail())
                .responseStatus(ResponseStatus.PENDING)
                .booking(booking)
                .build();

        when(bookingRepository.existsByProposedDateTime(any())).thenReturn(false);
        when(bookingRepository.save(any())).thenReturn(booking);
        when(inviteResponseRepository.save(any())).thenReturn(invite);

        BookingResponseDTO response = bookingService.createBooking(request);

        assertThat(response).isNotNull();
        assertThat(response.candidateName()).isEqualTo("John");
        assertThat(response.status()).isEqualTo(BookingStatus.PENDING);
        assertThat(response.platform()).isEqualTo(MeetingPlatform.GOOGLE);
        verify(emailService, times(1)).sendInvite(any(), any(), any());
    }

    @Test
    void shouldThrowExceptionIfTimeSlotAlreadyBooked() {
        BookingRequestDTO request = new BookingRequestDTO(
                "John",
                "Andy",
                LocalDateTime.now().plusDays(1),
                MeetingPlatform.TEAMS,
                "John@gmail.com"
        );

        when(bookingRepository.existsByProposedDateTime(any())).thenReturn(true);

        assertThrows(BookingException.class, () -> bookingService.createBooking(request));
        verify(bookingRepository, never()).save(any());
        verify(inviteResponseRepository, never()).save(any());
        verify(emailService, never()).sendInvite(any(), any(), any());
    }

    @Test
    void shouldRespondToInviteSuccessfully() {
        Long bookingId = 1L;

        Booking booking = Booking.builder()
                .id(bookingId)
                .candidateName("John")
                .interviewerName("Andy")
                .proposedDateTime(LocalDateTime.now().plusDays(1))
                .platform(MeetingPlatform.GOOGLE)
                .status(BookingStatus.PENDING)
                .build();

        InviteResponse invite = InviteResponse.builder()
                .id(1L)
                .recipientEmail("John@gmail.com")
                .responseStatus(ResponseStatus.PENDING)
                .booking(booking)
                .build();

        InviteResponseDTO dto = new InviteResponseDTO(
                "John@gmail.com",
                ResponseStatus.ACCEPTED,
                null
        );

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(inviteResponseRepository.findByBookingId(bookingId)).thenReturn(invite);
        when(inviteResponseRepository.save(any())).thenReturn(invite);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingResponseDTO response = bookingService.respondToInvite(bookingId, dto);

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(BookingStatus.ACCEPTED);
        assertThat(response.responses().get(0).responseStatus()).isEqualTo(ResponseStatus.ACCEPTED);
    }

    @Test
    void shouldThrowExceptionWhenBookingNotFoundOnRespond() {
        Long bookingId = 1L;
        InviteResponseDTO dto = new InviteResponseDTO(
                "John@gmail.com",
                ResponseStatus.ACCEPTED,
                null
        );

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(BookingException.class, () -> bookingService.respondToInvite(bookingId, dto));
    }

    @Test
    void shouldGetBookingStatusSuccessfully() {
        Long bookingId = 1L;

        Booking booking = Booking.builder()
                .id(bookingId)
                .candidateName("John")
                .interviewerName("Andy")
                .proposedDateTime(LocalDateTime.now().plusDays(1))
                .platform(MeetingPlatform.TEAMS)
                .status(BookingStatus.PENDING)
                .build();

        InviteResponse invite = InviteResponse.builder()
                .id(1L)
                .recipientEmail("John@gmail.com")
                .responseStatus(ResponseStatus.PENDING)
                .booking(booking)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(inviteResponseRepository.findByBookingId(bookingId)).thenReturn(invite);

        BookingResponseDTO response = bookingService.getBookingStatus(bookingId);

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(BookingStatus.PENDING);
        assertThat(response.platform()).isEqualTo(MeetingPlatform.TEAMS);
        assertThat(response.responses().get(0).recipientEmail()).isEqualTo("John@gmail.com");
    }

    @Test
    void shouldThrowExceptionWhenBookingNotFoundOnGetStatus() {
        Long bookingId = 1L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(BookingException.class, () -> bookingService.getBookingStatus(bookingId));
    }
}
