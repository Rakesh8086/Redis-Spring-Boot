package com.flight.bookingapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public class BookingRequest {

    @NotBlank
    private String userName;

    @NotBlank
    @Email(message = "Email must be a valid format.")
    private String userEmail;
    
    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits.")
    private String mobileNumber; 

    @NotNull
    @Pattern(regexp = "Veg|NonVeg", message = "Meal option must be 'Veg' or 'NonVeg'.")
    private String mealOpted; 
    
    @Valid // This one checks validation on the list elements on PassengerDTO
    @NotEmpty(message = "Passenger details are required for booking.")
    private List<PassengerDTO> passengers;

    public BookingRequest() {
    	
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

	public String getMealOpted() {
		return mealOpted;
	}

	public void setMealOpted(String mealOpted) {
		this.mealOpted = mealOpted;
	}

	public List<PassengerDTO> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<PassengerDTO> passengers) {
		this.passengers = passengers;
	}

}