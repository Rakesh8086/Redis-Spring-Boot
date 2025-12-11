package com.flight.bookingapp.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flight.bookingapp.dto.BookingRequest;
import com.flight.bookingapp.dto.PassengerDTO;
import com.flight.bookingapp.entity.Booking;
import com.flight.bookingapp.entity.Passenger;
import com.flight.bookingapp.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1.0/flight")
public class BookingController {
	
	@Autowired
	BookingService bookingService;
	
	@PostMapping("/booking/{flightId}")
    public ResponseEntity<Booking> bookTicket(
            @PathVariable Long flightId, 
            @Valid @RequestBody BookingRequest request) {
        
        Booking bookingEntity = mapBookingRequestToEntity(request);
        
        Booking bookedTicket = bookingService.bookTicket(flightId, bookingEntity);
        
        return new ResponseEntity<>(bookedTicket, HttpStatus.CREATED);
    }
    
    private Booking mapBookingRequestToEntity(BookingRequest request) {
        Booking booking = new Booking();
        booking.setUserName(request.getUserName());
        booking.setUserEmail(request.getUserEmail());
        booking.setMobileNumber(request.getMobileNumber());
        booking.setMealOpted(request.getMealOpted());
        
        // Map Passenger DTOs to Passenger Entities
        List<Passenger> passengers = request.getPassengers().stream()
                .map(this::mapPassengerDtoToEntity)
                .collect(Collectors.toList());
        
        booking.setPassengers(passengers); 
        
        return booking;
    }
    
    // Helper method to convert PassengerDTO to Passenger Entity.
    private Passenger mapPassengerDtoToEntity(PassengerDTO dto) {
        Passenger passenger = new Passenger();
        passenger.setName(dto.getName());
        passenger.setGender(dto.getGender());
        passenger.setAge(dto.getAge());
        passenger.setSeatNumber(dto.getSeatNumber());
        
        return passenger;
    }
    
    @GetMapping("/ticket/{pnr}")
    public ResponseEntity<Booking> getTicketByPnr(@PathVariable String pnr) {
        
        Booking booking = bookingService.getTicketByPnr(pnr);
        
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }
    
    @GetMapping("/booking/history/{emailId}")
    public ResponseEntity<List<Booking>> getBookingHistoryByEmail(
    		@PathVariable String emailId){
    	
    	List<Booking> history = bookingService.getBookingHistoryByEmail(emailId);
    	
    	return new ResponseEntity<>(history, HttpStatus.OK);
    }
    
    @DeleteMapping("/booking/cancel/{pnr}")
    public ResponseEntity<String> cancelTicket(@PathVariable String pnr) {
        
        bookingService.cancelTicket(pnr);
        
        return new ResponseEntity<>("Ticket with PNR " + pnr + 
        		" cancelled successfully.", HttpStatus.OK);
    }
}
