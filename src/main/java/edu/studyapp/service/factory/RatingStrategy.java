package edu.studyapp.service.factory;

import java.util.List;

public interface RatingStrategy<T> {
    List<T> calculateRating(String courseTitle);
}
