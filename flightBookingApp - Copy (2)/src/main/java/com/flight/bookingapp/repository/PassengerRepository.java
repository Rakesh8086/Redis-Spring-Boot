package com.flight.bookingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flight.bookingapp.entity.Passenger;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
	
}
