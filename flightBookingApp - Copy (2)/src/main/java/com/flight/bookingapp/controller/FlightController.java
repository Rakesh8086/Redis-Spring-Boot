package com.flight.bookingapp.controller;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.flight.bookingapp.dto.FlightSearchRequest;
import com.flight.bookingapp.entity.Flight;
import com.flight.bookingapp.service.FlightService;

@RestController
@RequestMapping("/api/v1.0/flight")
public class FlightController {
	
	@Autowired
    FlightService flightService;

    @PostMapping("/airline/inventory/add")
    public ResponseEntity<Flight> addFlightInventory(@Valid @RequestBody Flight flight) {
        
        Flight savedFlight = flightService.addFlight(flight);
        
        return new ResponseEntity<>(savedFlight, HttpStatus.CREATED);
    }
    
    @PostMapping("/search")
    public ResponseEntity<List<Flight>> searchFlights(@Valid @RequestBody FlightSearchRequest request) {
        
        List<Flight> matchingFlights = flightService.searchFlights(
                request.getFromPlace(),
                request.getToPlace(),
                request.getJourneyDate()
        );

        if(matchingFlights.isEmpty()) {
            // no flights available
        	throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, 
                    "No flights found matching the criteria from " + 
                    request.getFromPlace() + " to " + request.getToPlace() + 
                    " on " + request.getJourneyDate()
                );
        }

        return new ResponseEntity<>(matchingFlights, HttpStatus.OK);
    }
    
}