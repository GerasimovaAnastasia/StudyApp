package edu.studyapp.service.factory;



import java.util.List;

public class RatingContext {
    private final RatingStrategy<?> strategy;

    public RatingContext(RatingStrategy<?> strategy) {
        this.strategy = strategy;
    }

    public List<?> executeStrategy(String courseTitle) {
        return strategy.calculateRating(courseTitle);
    }
}
