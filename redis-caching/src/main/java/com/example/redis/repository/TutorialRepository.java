package com.example.redis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.redis.model.Tutorial;

@Repository
public interface TutorialRepository extends JpaRepository<Tutorial, Integer>{
  List<Tutorial> findByTitleContaining(String title);
  Optional<Tutorial> findById(Long id);
  List<Tutorial> findByPublished(boolean isPublished);
  List<Tutorial> findAll();
}
