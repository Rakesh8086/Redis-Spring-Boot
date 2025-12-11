package com.flight.bookingapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PassengerDTO {

    @NotBlank
    private String name;    

    @NotBlank
    private String gender;  

    @NotNull
    @Min(value = 0)
    private Integer age;    
    
    @NotBlank
    private String seatNumber;

    public PassengerDTO() {
    	
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}

    
}
