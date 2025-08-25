package edu.studyapp.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    @PersistenceContext
    private EntityManager entityManager;
}
