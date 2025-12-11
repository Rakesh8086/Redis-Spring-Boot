package com.flight.bookingapp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import com.flight.bookingapp.exception.BookingNotFoundException;
import com.flight.bookingapp.exception.CancellationNotPossibleException;
import com.flight.bookingapp.exception.FlightUnavailableException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName;
            if (error instanceof FieldError) {
                fieldName = ((FieldError) error).getField();
            } 
            else {
                fieldName = error.getObjectName(); 
            }
            String message = error.getDefaultMessage();
            errorMap.put(fieldName, message);
        });
        
        // 400 Bad request
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST); 
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        
        String errorMessage = "Invalid format. Please check date/time or number fields. " + 
                              ex.getMostSpecificCause().getMessage();
        
        // Return a 400 Bad Request 
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        // Return a reason for the exception
        return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
    }
    
    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<String> handleBookingNotFoundException(BookingNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FlightUnavailableException.class)
    public ResponseEntity<String> handleFlightUnavailableException(FlightUnavailableException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(CancellationNotPossibleException.class)
    public ResponseEntity<String> handleCancellationNotPossibleException(CancellationNotPossibleException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
