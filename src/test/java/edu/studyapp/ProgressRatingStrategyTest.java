package edu.studyapp;

import edu.studyapp.dto.StudentProgress;
import edu.studyapp.model.Student;
import edu.studyapp.model.StudentCourse;
import edu.studyapp.model.Task;
import edu.studyapp.service.CourseService;
import edu.studyapp.service.TaskService;
import edu.studyapp.service.factory.ProgressRatingStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressRatingStrategyTest {

    @Mock
    private TaskService taskService;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private ProgressRatingStrategy progressRatingStrategy;

    @Test
    void calculateRating_Success() {

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

        when(taskService.getAllTasksByCourse("Math")).thenReturn(List.of(task1, task2));
        when(courseService.getStudentsByCourse("Math")).thenReturn(List.of(sc1, sc2));
        when(taskService.getAllStudentAnswers("student1", "Math")).thenReturn(List.of());
        when(taskService.getAllStudentAnswers("student2", "Math")).thenReturn(List.of());

        List<StudentProgress> result = progressRatingStrategy.calculateRating("Math");

        assertEquals(2, result.size());
        assertEquals(0, result.get(0).completedTasks());
        assertEquals(0.0, result.get(0).progressPercent());
    }

    @Test
    void calculateRating_WithCompletedTasks() {

        Student student = new Student();
        student.setUsername("student1");
        StudentCourse sc = new StudentCourse();
        sc.setStudent(student);

        Task task1 = new Task();
        Task task2 = new Task();

        when(taskService.getAllTasksByCourse("Math")).thenReturn(List.of(task1, task2));
        when(courseService.getStudentsByCourse("Math")).thenReturn(List.of(sc));
        when(taskService.getAllStudentAnswers("student1", "Math")).thenReturn(List.of());

        List<StudentProgress> result = progressRatingStrategy.calculateRating("Math");

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).completedTasks());
        assertEquals(0.0, result.get(0).progressPercent());
    }

    @Test
    void calculateRating_NoTasks() {

        Student student = new Student();
        student.setUsername("student1");
        StudentCourse sc = new StudentCourse();
        sc.setStudent(student);

        when(taskService.getAllTasksByCourse("Math")).thenReturn(List.of());
        when(courseService.getStudentsByCourse("Math")).thenReturn(List.of(sc));
        when(taskService.getAllStudentAnswers("student1", "Math")).thenReturn(List.of());


        List<StudentProgress> result = progressRatingStrategy.calculateRating("Math");

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).completedTasks());
        assertEquals(0.0, result.get(0).progressPercent());
    }
}
