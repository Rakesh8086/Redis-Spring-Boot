package com.flight.bookingapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flight.bookingapp.entity.Flight;
import com.flight.bookingapp.repository.FlightRepository;
import com.flight.bookingapp.service.impl.FlightServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlightServiceTest {

    // Mocks the FlightRepository dependency
    @Mock
    private FlightRepository flightRepository;

    // Injects the Mocks into the Service
    @InjectMocks
    private FlightServiceImpl flightService;

    private Flight testFlight;

    // method to set up a reusable Flight object before each test
    @BeforeEach
    void setUp() {
        testFlight = new Flight();
        testFlight.setId(101L);
        testFlight.setAirlineName("Air India");
        testFlight.setFromPlace("DEL");
        testFlight.setToPlace("BOM");
        testFlight.setScheduleDate(LocalDate.of(2025, 12, 25));
        testFlight.setDepartureTime(LocalTime.of(8, 0));
        testFlight.setArrivalTime(LocalTime.of(10, 0));
        testFlight.setPrice(5000.00);
        testFlight.setTotalSeats(150);
        testFlight.setAvailableSeats(150);
    }

    // Testing addFlight

    @Test
    void addFlight_Success_InitializesAvailableSeats() {
        when(flightRepository.save(any(Flight.class))).thenReturn(testFlight);

        Flight savedFlight = flightService.addFlight(testFlight);

        assertNotNull(savedFlight);
        assertEquals(150, savedFlight.getAvailableSeats(), 
        		"Available seats should be initialized to total seats.");
        // checking if 150 seats are correctly initialized as we expect
        verify(flightRepository, times(1)).save(testFlight);
    }

    // Testing searchFlights
    
    @Test
    void searchFlights_Success_ReturnsMatchingFlights() {
        String from = "DEL";
        String to = "BOM";
        LocalDate date = LocalDate.of(2025, 12, 25);
        
        when(flightRepository.findByFromPlaceAndToPlaceAndScheduleDateAndAvailableSeatsGreaterThan(
                from, to, date, 0)).thenReturn(List.of(testFlight));

        List<Flight> results = flightService.searchFlights(from, to, date);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size()); // we are finding exactly 1 entry as we expect
        assertEquals(testFlight.getId(), results.get(0).getId());
        
        verify(flightRepository, times(1)).findByFromPlaceAndToPlaceAndScheduleDateAndAvailableSeatsGreaterThan(
                from, to, date, 0);
    }

    @Test
    void searchFlights_EmptyResult_NoMatchingFlights() {
        String from = "DEL";
        String to = "CHE";
        LocalDate date = LocalDate.of(2025, 12, 25);

        when(flightRepository.findByFromPlaceAndToPlaceAndScheduleDateAndAvailableSeatsGreaterThan(
                from, to, date, 0)).thenReturn(Collections.emptyList());

        List<Flight> results = flightService.searchFlights(from, to, date);
        // we expect no the list to be empty since there is no such flight
        assertTrue(results.isEmpty());
    }
    
    // Testing getFlightById

    @Test
    void getFlightById_Success_ReturnsFlight() {
        when(flightRepository.findById(101L)).thenReturn(Optional.of(testFlight));

        Optional<Flight> found = flightService.getFlightById(101L);
        
        // check if we are finding a flight entry 101 which we added
        assertTrue(found.isPresent());
        assertEquals(testFlight.getId(), found.get().getId());
    }

    @Test
    void getFlightById_NotFound_ReturnsEmptyOptional() {
        when(flightRepository.findById(102L)).thenReturn(Optional.empty());

        Optional<Flight> found = flightService.getFlightById(102L);
        
        // ensure we are not finding a flight with 102 which we did not add
        assertFalse(found.isPresent());
    }
    
    // Testing updateFlightInventory
    
    @Test
    void updateFlightInventory_Success() {
        testFlight.setAvailableSeats(50);
        when(flightRepository.save(testFlight)).thenReturn(testFlight);
        
        Flight updated = flightService.updateFlightInventory(testFlight);
        
        assertEquals(50, updated.getAvailableSeats());
        
        verify(flightRepository, times(1)).save(testFlight);
    }
}
