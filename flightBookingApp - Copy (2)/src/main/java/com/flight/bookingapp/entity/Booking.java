package com.flight.bookingapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    private String pnr; 
    
    @NotBlank
    private String userName;
    @NotBlank
    @Email(message = "Email must be a valid format.")
    private String userEmail;
    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits.")
    private String mobileNumber;
    @NotNull
    private LocalDateTime bookingDate;
    @Min(value = 1, message = "Must book at least 1 seat.")
    private Integer numberOfSeats;
    @NotBlank
    private String mealOpted;
    private Double totalCost;

    // Storing the Flight ID as a foreign key for easy lookup
    @NotNull
    private Long flightId;
    
    // We store the journey date here for easy cancellation checks 
    @NotNull
    private LocalDate journeyDate; 

    // One Booking can have many Passengers.
    // CascadeType.ALL ensures that if a Booking is deleted, its Passengers are also deleted.
    // Passenger entity owns the relationship
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Passenger> passengers;

    public Booking() {
    	
    }

	public String getPnr() {
		return pnr;
	}

	public void setPnr(String pnr) {
		this.pnr = pnr;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public LocalDateTime getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(LocalDateTime bookingDate) {
		this.bookingDate = bookingDate;
	}

	public Integer getNumberOfSeats() {
		return numberOfSeats;
	}

	public void setNumberOfSeats(Integer numberOfSeats) {
		this.numberOfSeats = numberOfSeats;
	}

	public String getMealOpted() {
		return mealOpted;
	}

	public void setMealOpted(String mealOpted) {
		this.mealOpted = mealOpted;
	}

	public Double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}

	public Long getFlightId() {
		return flightId;
	}

	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}

	public LocalDate getJourneyDate() {
		return journeyDate;
	}

	public void setJourneyDate(LocalDate journeyDate) {
		this.journeyDate = journeyDate;
	}

	public List<Passenger> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<Passenger> passengers) {
		this.passengers = passengers;
	}

}
