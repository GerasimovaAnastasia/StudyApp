package edu.studyapp;

import edu.studyapp.dto.StudentRating;
import edu.studyapp.model.Student;
import edu.studyapp.model.StudentAnswer;
import edu.studyapp.model.StudentCourse;
import edu.studyapp.model.Task;
import edu.studyapp.service.CourseService;
import edu.studyapp.service.TaskService;
import edu.studyapp.service.factory.GradeRatingStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeRatingStrategyTest {

    @Mock
    private TaskService taskService;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private GradeRatingStrategy gradeRatingStrategy;

    @Test
    void calculateRating_Success() {

        Student student = new Student();
        student.setUsername("student1");
        StudentCourse sc = new StudentCourse();
        sc.setStudent(student);

        Task task1 = new Task();
        Task task2 = new Task();

        StudentAnswer answer1 = new StudentAnswer();
        answer1.setGrade(5);
        StudentAnswer answer2 = new StudentAnswer();
        answer2.setGrade(4);

        when(taskService.getAllTasksByCourse("Math")).thenReturn(List.of(task1, task2));
        when(courseService.getStudentsByCourseStatus("Math")).thenReturn(List.of(sc));
        when(taskService.getAllStudentAnswers("student1", "Math")).thenReturn(List.of(answer1, answer2));

        List<StudentRating> result = gradeRatingStrategy.calculateRating("Math");

        assertEquals(1, result.size());
        assertEquals(4.5, result.get(0).grade());
    }

    @Test
    void calculateRating_NoAnswers_ReturnsZeroGrade() {

        Student student = new Student();
        student.setUsername("student1");
        StudentCourse sc = new StudentCourse();
        sc.setStudent(student);

        when(courseService.getStudentsByCourseStatus("Math")).thenReturn(List.of(sc));
        when(taskService.getAllStudentAnswers("student1", "Math")).thenReturn(List.of());

        List<StudentRating> result = gradeRatingStrategy.calculateRating("Math");

        assertEquals(1, result.size());
        assertEquals(0.0, result.get(0).grade());
        assertEquals("student1", result.get(0).student().getUsername());
    }

    @Test
    void calculateRating_MultipleStudents() {
        Student student1 = new Student();
        student1.setUsername("student1");
        Student student2 = new Student();
        student2.setUsername("student2");

        StudentCourse sc1 = new StudentCourse();
        sc1.setStudent(student1);
        StudentCourse sc2 = new StudentCourse();
        sc2.setStudent(student2);

        Task task1 = new Task();
        Task task2 = new Task();

        StudentAnswer answer1 = new StudentAnswer();
        answer1.setGrade(5);
        StudentAnswer answer2 = new StudentAnswer();
        answer2.setGrade(3);

        when(taskService.getAllTasksByCourse("Math")).thenReturn(List.of(task1, task2));
        when(courseService.getStudentsByCourseStatus("Math")).thenReturn(List.of(sc1, sc2));
        when(taskService.getAllStudentAnswers("student1", "Math")).thenReturn(List.of(answer1));
        when(taskService.getAllStudentAnswers("student2", "Math")).thenReturn(List.of(answer2));

        List<StudentRating> result = gradeRatingStrategy.calculateRating("Math");

        assertEquals(2, result.size());
        assertEquals(5.0, result.get(0).grade());
        assertEquals(3.0, result.get(1).grade());
    }
}