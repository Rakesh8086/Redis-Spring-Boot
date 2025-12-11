package com.flight.bookingapp.exception;

//Used when cancelling a booking with an wrong PNR
public class BookingNotFoundException extends RuntimeException {
	public BookingNotFoundException(String message) {
		super(message);
	}
}
