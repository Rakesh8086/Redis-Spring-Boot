package com.flight.bookingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flight.bookingapp.entity.Flight;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    // We check if there are seats available before executing this custom query.
    List<Flight> findByFromPlaceAndToPlaceAndScheduleDateAndAvailableSeatsGreaterThan(
            String fromPlace, 
            String toPlace, 
            LocalDate scheduleDate, 
            int availableSeats
    );
}