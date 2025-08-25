package edu.studyapp.service.factory;

import edu.studyapp.dto.StudentRating;
import edu.studyapp.model.StudentAnswer;
import edu.studyapp.model.StudentCourse;
import edu.studyapp.service.CourseFileService;
import edu.studyapp.service.CourseService;
import edu.studyapp.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class GradeRatingStrategy implements RatingStrategy<StudentRating> {
    private final TaskService taskService;
    private final CourseService courseService;
    private static final Logger logger = LoggerFactory.getLogger(GradeRatingStrategy.class);

    public GradeRatingStrategy(TaskService taskService, CourseService courseService) {
        this.taskService = taskService;
        this.courseService = courseService;
    }
    @Override
    public List<StudentRating> calculateRating (String courseTitle) {
        logger.info("Расчет рейтинга через GradeRatingStrategy по курсу: {}", courseTitle);

        int totalTasks = taskService.getAllTasksByCourse(courseTitle).size();
        List<StudentCourse> studentsByCourse = courseService.getStudentsByCourseStatus(courseTitle);
        return studentsByCourse.stream()
                .map(studentCourse -> {
                    List<StudentAnswer> studentAnswers = taskService.getAllStudentAnswers(studentCourse.getStudent().getUsername(), courseTitle);

                    if (studentAnswers.isEmpty()) {
                        return new StudentRating(
                                studentCourse.getStudent(),
                                0.0
                        );
                    }

                    double averageGrade = studentAnswers.stream()
                            .mapToInt(StudentAnswer::getGrade)
                            .average()
                            .orElse(0.0);

                    return new StudentRating(
                            studentCourse.getStudent(),
                            averageGrade
                    );
                })
                .sorted(Comparator.comparingDouble(StudentRating::grade).reversed())
                .collect(Collectors.toList());
    }
}
