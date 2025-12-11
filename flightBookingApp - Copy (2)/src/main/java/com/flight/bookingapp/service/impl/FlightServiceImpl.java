package com.flight.bookingapp.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight.bookingapp.entity.Flight;
import com.flight.bookingapp.repository.FlightRepository;
import com.flight.bookingapp.service.FilesStorageService;
import com.flight.bookingapp.service.FlightService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;


@Service
public class FlightServiceImpl implements FlightService {
	
	@Autowired
    private FlightRepository flightRepository;
	@Autowired
	private FilesStorageService storageService;
	@Autowired
    private ObjectMapper objectMapper;
	@Autowired
	private Validator validator;

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
    
    @Override
    public List<Flight> addFlights(String filename) throws IOException{
    	Resource file = storageService.load(filename);
        List<Flight> flights =
                objectMapper.readValue(file.getInputStream(), new TypeReference<List<Flight>>() {});
        List<Flight> valid = new ArrayList<>();
        List<Flight> invalid = new ArrayList<>();
        for(Flight f : flights) {
            Set<ConstraintViolation<Flight>> violations = validator.validate(f);
            f.setAvailableSeats(f.getTotalSeats()); 
            if(violations.isEmpty()) {             
            	valid.add(f);
            }
            else {
            	invalid.add(f);
            }
        }

        flightRepository.saveAll(valid);
        return invalid;
    }
}
