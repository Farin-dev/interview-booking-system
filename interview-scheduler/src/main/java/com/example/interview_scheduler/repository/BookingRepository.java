package com.example.interview_scheduler.repository;

import com.example.interview_scheduler.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByProposedDateTime(LocalDateTime proposedDateTime);
}
