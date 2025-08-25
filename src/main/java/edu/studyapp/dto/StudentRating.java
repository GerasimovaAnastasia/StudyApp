package edu.studyapp.dto;

import edu.studyapp.model.Student;

public record StudentRating(Student student,
                            double grade) {
    @Override
    public double grade() {
        return grade;
    }
}
