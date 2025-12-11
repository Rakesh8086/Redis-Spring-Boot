package com.flight.bookingapp.service;

import java.util.List;

import com.flight.bookingapp.entity.Booking;

public interface BookingService {

    // used for the ticket booking process. 
	// Booking object contains passenger details
    Booking bookTicket(Long flightId, Booking booking);
    
    Booking getTicketByPnr(String pnr);
    
    List<Booking> getBookingHistoryByEmail(String emailId);
    
    void cancelTicket(String pnr);
}