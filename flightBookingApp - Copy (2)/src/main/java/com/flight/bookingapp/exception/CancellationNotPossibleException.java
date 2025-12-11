package com.flight.bookingapp.exception;

public class CancellationNotPossibleException extends RuntimeException{
	public CancellationNotPossibleException(String message) {
	    super(message);
	}
}
