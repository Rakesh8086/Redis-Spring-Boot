package com.spring.login.controller;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.login.model.ERole;
import com.spring.login.model.Role;
import com.spring.login.model.User;
import com.spring.login.model.response.MessageResponse;
import com.spring.login.repository.RoleRepository;
import com.spring.login.repository.UserRepository;
import com.spring.login.request.SignupRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600) // * used for public end points
public class AuthController {
	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	PasswordEncoder encoder;
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(
			@Valid @RequestBody SignupRequest signUpRequest) {
	    if(userRepository.existsByUsername(signUpRequest.getUsername())) {
	      return ResponseEntity.badRequest().body(new MessageResponse(
	    		  "Username is already taken"));
	    }

	    if(userRepository.existsByEmail(signUpRequest.getEmail())) {
	      return ResponseEntity.badRequest().body(new MessageResponse(
	    		  "Email is already in use"));
	    }

	    // Create new account
	    User user = new User(signUpRequest.getUsername(),
	                         signUpRequest.getEmail(),
	                         encoder.encode(signUpRequest.getPassword()));

	    Set<String> allRoles = signUpRequest.getRole();
	    Set<Role> roles = new HashSet<>(); // to remove duplicates

	    if(allRoles == null) {
	      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
	          .orElseThrow(() -> new RuntimeException("Role is not found."));
	      roles.add(userRole);
	    } 
	    else {
	    	for(String role:allRoles) {
	    	    if("ROLE_ADMIN".equals(role)) {
	    	        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
	    	            .orElseThrow(() -> new RuntimeException("Role is not found."));
	    	        roles.add(adminRole);

	    	    } 
	    	    else if("ROLE_MODERATOR".equals(role)) {
	    	        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
	    	            .orElseThrow(() -> new RuntimeException("Role is not found."));
	    	        roles.add(modRole);

	    	    } 
	    	    else {
	    	        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
	    	            .orElseThrow(() -> new RuntimeException("Role is not found."));
	    	        roles.add(userRole);
	    	    }
	    	}
	    }

	    user.setRoles(roles);
	    userRepository.save(user);

	    return ResponseEntity.ok(new MessageResponse("User registered successfully"));
	}
}
