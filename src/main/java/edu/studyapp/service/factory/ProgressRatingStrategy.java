package edu.studyapp.service.factory;

import edu.studyapp.dto.StudentProgress;
import edu.studyapp.model.Student;
import edu.studyapp.model.StudentCourse;
import edu.studyapp.service.CourseService;
import edu.studyapp.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Service
public class ProgressRatingStrategy implements RatingStrategy<StudentProgress>{
    private final TaskService taskService;
    private final CourseService courseService;
    private static final Logger logger = LoggerFactory.getLogger(ProgressRatingStrategy.class);

    public ProgressRatingStrategy(TaskService taskService, CourseService courseService) {
        this.taskService = taskService;
        this.courseService = courseService;
    }
    @Override
    public List<StudentProgress> calculateRating(String courseTitle) {
        logger.info("Расчет рейтинга через ProgressRatingStrategy по курсу: {}", courseTitle);

        int totalTasks = taskService.getAllTasksByCourse(courseTitle).size();
        List<StudentCourse> studentsByCourse = courseService.getStudentsByCourse(courseTitle);

        return studentsByCourse.stream()
                .map(sc -> {
                    Student student = sc.getStudent();
                    int completedTasks = taskService.getAllStudentAnswers(student.getUsername(), courseTitle).size();
                    double progress = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;

                    return new StudentProgress(
                            student,
                            completedTasks,
                            progress
                    );
                })
                .sorted(Comparator.comparingInt(StudentProgress::completedTasks).reversed())
                .collect(Collectors.toList());
    }


}
