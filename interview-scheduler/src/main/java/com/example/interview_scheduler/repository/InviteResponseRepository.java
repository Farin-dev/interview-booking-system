package com.example.interview_scheduler.repository;

import com.example.interview_scheduler.model.entity.InviteResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InviteResponseRepository extends JpaRepository<InviteResponse, Long> {

    InviteResponse findByBookingId(Long bookingId);

}
