package com.flight.bookingapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flight.bookingapp.entity.Booking;
import com.flight.bookingapp.entity.Flight;
import com.flight.bookingapp.entity.Passenger;
import com.flight.bookingapp.exception.BookingNotFoundException;
import com.flight.bookingapp.exception.CancellationNotPossibleException;
import com.flight.bookingapp.exception.FlightUnavailableException;
import com.flight.bookingapp.repository.BookingRepository;
import com.flight.bookingapp.service.impl.BookingServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private FlightService flightService; 

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Flight testFlight;
    private Booking testBooking;
    private List<Passenger> passengers;
    private final Long TEST_FLIGHT_ID = 101L;
    private final String TEST_PNR = "FLBOOK123456";

    @BeforeEach
    void setUp() {
        testFlight = new Flight();
        testFlight.setId(TEST_FLIGHT_ID);
        testFlight.setTotalSeats(150);
        testFlight.setAvailableSeats(10);
        testFlight.setPrice(100.00);
        testFlight.setScheduleDate(LocalDate.now().plusDays(5));
        testFlight.setDepartureTime(LocalTime.of(10, 0)); // 10 AM

        Passenger p1 = new Passenger();
        p1.setName("Alice");
        p1.setBooking(testBooking); 
        Passenger p2 = new Passenger();
        p2.setName("Bob");
        p2.setBooking(testBooking); 
        passengers = List.of(p1, p2); // 2 seats requested

        testBooking = new Booking();
        testBooking.setFlightId(TEST_FLIGHT_ID);
        testBooking.setPassengers(passengers);
        testBooking.setNumberOfSeats(passengers.size());
        testBooking.setUserEmail("test@example.com");
    }

    // Testing bookTicket

    @Test
    void bookTicket_Success_InventoryDecrementedAndBookingSaved() {
        int seatsToBook = passengers.size(); 
        int initialAvailable = testFlight.getAvailableSeats();
        
        // Mock FlightService call
        when(flightService.getFlightById(TEST_FLIGHT_ID)).thenReturn(Optional.of(testFlight));
        
        // Mock FlightService update 
        when(flightService.updateFlightInventory(any(Flight.class))).thenReturn(testFlight);

        // Mock BookingRepository save
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        Booking booked = bookingService.bookTicket(TEST_FLIGHT_ID, testBooking);

        assertNotNull(booked.getPnr()); // PNR must be generated
        assertEquals(200.00, booked.getTotalCost(), "Total cost should be calculated 200.00");
        assertEquals(initialAvailable - seatsToBook, testFlight.getAvailableSeats(), "Inventory should be decremented");
        
        verify(flightService, times(1)).getFlightById(TEST_FLIGHT_ID);
        verify(flightService, times(1)).updateFlightInventory(testFlight);
        verify(bookingRepository, times(1)).save(testBooking);
    }

    @Test
    void bookTicket_Failure_FlightNotFound() {
        when(flightService.getFlightById(TEST_FLIGHT_ID)).thenReturn(Optional.empty());

        assertThrows(FlightUnavailableException.class, () -> 
            bookingService.bookTicket(TEST_FLIGHT_ID, testBooking));
        
        verify(flightService, never()).updateFlightInventory(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void bookTicket_Failure_InsufficientSeats() {
        testFlight.setAvailableSeats(1); // Only 1 seat available
		int seatsToBook = passengers.size(); // 2 seats requested
        
        when(flightService.getFlightById(TEST_FLIGHT_ID)).thenReturn(Optional.of(testFlight));

        FlightUnavailableException exception = assertThrows(FlightUnavailableException.class, () -> 
            bookingService.bookTicket(TEST_FLIGHT_ID, testBooking));
        
        // Assert the error message contains the seat counts
        assertTrue(exception.getMessage().contains("Insufficient seats available"));
        
        verify(flightService, never()).updateFlightInventory(any());
        verify(bookingRepository, never()).save(any());
    }
    
    // Testing getTicketByPnr

    @Test
    void getTicketByPnr_Success() {
        testBooking.setPnr(TEST_PNR);
        when(bookingRepository.findByPnr(TEST_PNR)).thenReturn(Optional.of(testBooking));

        Booking found = bookingService.getTicketByPnr(TEST_PNR);

        assertEquals(TEST_PNR, found.getPnr());
    }

    @Test
    void getTicketByPnr_Failure_NotFound() {
        when(bookingRepository.findByPnr(anyString())).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.getTicketByPnr("NONEXISTENT"));
    }
    
    // Testing getBookingHistoryByEmail

    @Test
    void getBookingHistoryByEmail_Success() {
        when(bookingRepository.findByUserEmailOrderByBookingDateDesc(anyString())).thenReturn(List.of(testBooking));

        List<Booking> history = bookingService.getBookingHistoryByEmail("test@example.com");

        assertFalse(history.isEmpty());
        assertEquals(1, history.size());
    }

    @Test
    void getBookingHistoryByEmail_Failure_NotFound() {
        when(bookingRepository.findByUserEmailOrderByBookingDateDesc(anyString())).thenReturn(List.of());

        assertThrows(BookingNotFoundException.class, () -> 
            bookingService.getBookingHistoryByEmail("abc@example.com"));
    }
    
    // Testing cancelTicket

    @Test
    void cancelTicket_Success_InventoryIncremented() {
        testBooking.setPnr(TEST_PNR);
        // Set journey date far in the future to pass the time check of 24 hr
        LocalDate futureDate = LocalDate.now().plusDays(10);
        testBooking.setJourneyDate(futureDate);
        testFlight.setScheduleDate(futureDate); 
        
        int initialAvailable = testFlight.getAvailableSeats(); // 10
        int seatsCancelled = testBooking.getNumberOfSeats(); // 2

        // Mock Booking lookup
        when(bookingRepository.findByPnr(TEST_PNR)).thenReturn(Optional.of(testBooking));
        
        // Mock Flight lookup
        when(flightService.getFlightById(TEST_FLIGHT_ID)).thenReturn(Optional.of(testFlight));

        bookingService.cancelTicket(TEST_PNR);

        assertEquals(initialAvailable + seatsCancelled, testFlight.getAvailableSeats(), "Inventory should be incremented");
        
        verify(flightService, times(1)).updateFlightInventory(testFlight); // Inventory updated
        verify(bookingRepository, times(1)).delete(testBooking); // Booking deleted
    }

    @Test
    void cancelTicket_Failure_BookingNotFound() {
        when(bookingRepository.findByPnr(anyString())).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.cancelTicket(TEST_PNR));
        
        verify(flightService, never()).getFlightById(any());
    }
    
    @Test
    void cancelTicket_Failure_DeadlineExceeded() {
        testBooking.setPnr(TEST_PNR);
        LocalDateTime pastDeparture = LocalDateTime.now().minusHours(23);
        testBooking.setJourneyDate(pastDeparture.toLocalDate());
        testFlight.setDepartureTime(pastDeparture.toLocalTime()); 
        
        when(bookingRepository.findByPnr(TEST_PNR)).thenReturn(Optional.of(testBooking));
        when(flightService.getFlightById(TEST_FLIGHT_ID)).thenReturn(Optional.of(testFlight));

        assertThrows(CancellationNotPossibleException.class, () 
         		-> bookingService.cancelTicket(TEST_PNR));
        
        verify(flightService, never()).updateFlightInventory(any());
        verify(bookingRepository, never()).delete(any());
    }
}
