package com.flight.bookingapp.repository;

import com.flight.bookingapp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
	
	// we are using optional, because it can return NULL as well
    Optional<Booking> findByPnr(String pnr);
    
    // Custom query to retrieve the booking history for a user using emailId.
    List<Booking> findByUserEmailOrderByBookingDateDesc(String userEmail);
}
