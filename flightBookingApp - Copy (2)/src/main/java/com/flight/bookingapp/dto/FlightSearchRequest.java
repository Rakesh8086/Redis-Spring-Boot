package com.flight.bookingapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class FlightSearchRequest {

    @NotBlank
    private String fromPlace;
    @NotBlank
    private String toPlace;
    @NotNull
    private LocalDate journeyDate;
   
    
    public FlightSearchRequest() {
    	
    }

    public String getFromPlace() {
        return fromPlace;
    }

    public void setFromPlace(String fromPlace) {
        this.fromPlace = fromPlace;
    }

    public String getToPlace() {
        return toPlace;
    }

    public void setToPlace(String toPlace) {
        this.toPlace = toPlace;
    }

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

}
