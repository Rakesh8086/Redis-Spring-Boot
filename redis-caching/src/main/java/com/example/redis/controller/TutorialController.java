package com.example.redis.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.redis.model.Tutorial;
import com.example.redis.service.TutorialService;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class TutorialController {
	@Autowired
	TutorialService tutorialService;
	
	@GetMapping("/tutorials/{id}")
	  @ResponseStatus(HttpStatus.OK)
	  public Optional<Tutorial> getTutorialById(@PathVariable("id") int id) {
	    return tutorialService.getTutorialById(id);
	}
	
	@PostMapping("/tutorials")
	  @ResponseStatus(HttpStatus.CREATED)
	  public Tutorial createTutorial(@RequestBody Tutorial tutorial) {
	    return tutorialService.save(new Tutorial(0, tutorial.getTitle(), tutorial.getDescription(), false));
	}
}
