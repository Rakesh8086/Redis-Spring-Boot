package com.flight.bookingapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.flight.bookingapp.service.FilesStorageService;

import jakarta.annotation.Resource;

@SpringBootApplication
public class FlightBookingAppApplication implements CommandLineRunner{
	@Resource
	FilesStorageService storageService;
	
	public static void main(String[] args) {
		SpringApplication.run(FlightBookingAppApplication.class, args);
	}
	
	@Override
    public void run(String... arg) throws Exception {
//	    storageService.deleteAll();
		storageService.init();
    }
}
