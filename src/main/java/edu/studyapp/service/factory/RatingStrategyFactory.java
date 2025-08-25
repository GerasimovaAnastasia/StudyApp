package edu.studyapp.service.factory;

public interface RatingStrategyFactory {
    RatingStrategy<?> getStrategy(String type);
}
