package com.flight.bookingapp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flight.bookingapp.entity.Booking;
import com.flight.bookingapp.entity.Flight;
import com.flight.bookingapp.entity.Passenger;
import com.flight.bookingapp.exception.BookingNotFoundException;
import com.flight.bookingapp.exception.CancellationNotPossibleException;
import com.flight.bookingapp.exception.FlightUnavailableException;
import com.flight.bookingapp.repository.BookingRepository;
import com.flight.bookingapp.service.BookingService;
import com.flight.bookingapp.service.FlightService;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
	
	@Autowired
    FlightService flightService;
	@Autowired
    BookingRepository bookingRepository;

    public BookingServiceImpl(FlightService flightService, BookingRepository bookingRepository) {
        this.flightService = flightService;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional // inventory update and booking save happen together.
    public Booking bookTicket(Long flightId, Booking requestBooking) {
        
    	// Check for available flight.
        Flight flight = flightService.getFlightById(flightId)
                .orElseThrow(() -> new FlightUnavailableException(
                		"Flight with ID " + flightId + " not found."));
        
        int seatsToBook = requestBooking.getPassengers().size();
        
        if(seatsToBook <= 0) {
            throw new FlightUnavailableException(
            		"Number of seats must be at least one.");
        }        
        
        Integer currentAvailableSeats = flight.getAvailableSeats();
        
        if(currentAvailableSeats < seatsToBook) {
            throw new FlightUnavailableException(
                    "Insufficient seats available. Requested: " + seatsToBook + 
                    ", Available: " + currentAvailableSeats
            );
        }
        
        // Decrement available seats
        flight.setAvailableSeats(currentAvailableSeats - seatsToBook);
        flightService.updateFlightInventory(flight); 

        
        // Generate PNR 
        String pnr = generateUniquePNR(); 
        requestBooking.setPnr(pnr); 
        
        requestBooking.setFlightId(flightId);
        requestBooking.setBookingDate(LocalDateTime.now());
        requestBooking.setNumberOfSeats(seatsToBook);
        requestBooking.setTotalCost(flight.getPrice() * seatsToBook);
        requestBooking.setJourneyDate(flight.getScheduleDate());

        List<Passenger> passengers = requestBooking.getPassengers().stream()
                .map(passenger -> {
                    // perform ManyToOne mapping
                    passenger.setBooking(requestBooking); 
                    return passenger;
                })
                .collect(Collectors.toList());
        
        requestBooking.setPassengers(passengers);
        
        // Save booking and passengers 
        return bookingRepository.save(requestBooking);
    }
    
    private String generateUniquePNR() {
        return "CHUBBFLIGHT" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
    
    @Override
    public Booking getTicketByPnr(String pnr) {
        return bookingRepository.findByPnr(pnr)
                .orElseThrow(() -> new BookingNotFoundException("Ticket with PNR " + pnr + " not found."));
    }
    
    @Override
    public List<Booking> getBookingHistoryByEmail(String emailId) {
        List<Booking> history = bookingRepository.findByUserEmailOrderByBookingDateDesc(emailId);
        
        if(history.isEmpty()) {
            throw new BookingNotFoundException(
            		"No booking history found for email: " + emailId);
        }
        
        return history;
    }
    
    @Override
    @Transactional
    public void cancelTicket(String pnr) {
        
        Booking booking = getTicketByPnr(pnr);
        
        Long flightId = booking.getFlightId();
        int cancelledSeats = booking.getNumberOfSeats();
        Flight flight = flightService.getFlightById(flightId)
                .orElseThrow(() -> new FlightUnavailableException(
                		"Flight inventory not found for ID " + flightId));
        
        // Check if the current time is AFTER the deadline
        LocalDateTime departureTime = booking.getJourneyDate().
        		atTime(flight.getDepartureTime());
        LocalDateTime cancellationDeadline = departureTime.minus(
        		24, ChronoUnit.HOURS);
        
        if(LocalDateTime.now().isAfter(cancellationDeadline)) {
            throw new CancellationNotPossibleException(
                "Cancellation failed. Tickets must be cancelled at least "
                + "24 hours prior to departure time");
        }
        
        // Increment Inventory
        flight.setAvailableSeats(flight.getAvailableSeats() + cancelledSeats);
        flightService.updateFlightInventory(flight);

        // Delete the Booking Record (or update status)
        bookingRepository.delete(booking);
    }
}
