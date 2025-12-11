package com.flight.bookingapp.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.flight.bookingapp.entity.Flight;

public interface FlightService {

    // used for adding flight
    Flight addFlight(Flight flight);
    
    // for searching flights
    List<Flight> searchFlights(String fromPlace, String toPlace, LocalDate scheduleDate);
    
    // get flight by its ID
    Optional<Flight> getFlightById(Long flightId);
    
    // to update flight seats after booking tickets
    Flight updateFlightInventory(Flight flight);
}
