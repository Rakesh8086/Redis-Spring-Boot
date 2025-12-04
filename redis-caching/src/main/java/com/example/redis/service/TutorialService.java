package com.example.redis.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import com.example.redis.model.Tutorial;
import com.example.redis.repository.TutorialRepository;

@Service
@EnableCaching
public class TutorialService {
  @Autowired
  TutorialRepository tutorialRepository;

  @Cacheable("tutorial")
  public Optional<Tutorial> getTutorialById(long id) {
    doLongRunningTask();

    return tutorialRepository.findById(id);
  }
  
  public Tutorial save(Tutorial tutorial) {
	    return tutorialRepository.save(tutorial);
	  }

  private void doLongRunningTask() {
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
