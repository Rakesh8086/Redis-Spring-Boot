package com.flight.bookingapp.service.impl;

import com.flight.bookingapp.service.FlightService;
import com.flight.bookingapp.entity.Flight;
import com.flight.bookingapp.repository.FlightRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FlightServiceImpl implements FlightService {
	
	@Autowired
    FlightRepository flightRepository;

    @Override
    public Flight addFlight(Flight flight) {
    	flight.setAvailableSeats(flight.getTotalSeats());
        
        return flightRepository.save(flight);
    }
    
    @Override
    public List<Flight> searchFlights(String fromPlace, String toPlace, LocalDate scheduleDate) {
        return flightRepository.findByFromPlaceAndToPlaceAndScheduleDateAndAvailableSeatsGreaterThan(
                fromPlace, 
                toPlace, 
                scheduleDate, 
                0 // show flights with 1 or more available seats
        );
    }
    
    @Override
    public Optional<Flight> getFlightById(Long flightId) {
        return flightRepository.findById(flightId);
    }
    
    @Override
    public Flight updateFlightInventory(Flight flight) {
        return flightRepository.save(flight);
    }
}
