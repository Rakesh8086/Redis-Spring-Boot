package com.example.redis.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity // Marks this class as a JPA entity
@Table(name = "tutorials") 
public class Tutorial {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String title;

  private String description;

  private boolean published;
  
}