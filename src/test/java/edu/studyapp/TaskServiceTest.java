package edu.studyapp;

import edu.studyapp.dto.TaskRequest;
import edu.studyapp.model.*;
import edu.studyapp.repository.*;
import edu.studyapp.service.RegisterService;
import edu.studyapp.service.TaskService;
import edu.studyapp.service.exception.EntityNotFoundException;
import edu.studyapp.service.exception.ValidateHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private RegisterService registerService;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentAnswerRepository studentAnswerRepository;

    @Mock
    private ValidateHandler validateHandler;

    @Mock
    private StudentCourseRepository studentCourseRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void addTask_Success() {
        TaskRequest request = new TaskRequest("Task 1",
                "Content",
                "Course");
        Course course = new Course();
        course.setTitle("Math");

        when(courseRepository.findByTitle("Math")).thenReturn(Optional.of(course));

        taskService.addTask("Math", request);

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void getTask_Success() {

        Task task = new Task();
        task.setTitle("Task 1");

        when(taskRepository.findByTitle("Task 1", "Math")).thenReturn(Optional.of(task));

        Task result = taskService.getTask("Task 1", "Math");

        assertNotNull(result);
        assertEquals("Task 1", result.getTitle());
    }

    @Test
    void getTask_NotFound_ThrowsException() {

        when(taskRepository.findByTitle("Unknown", "Math")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            taskService.getTask("Unknown", "Math");
        });
    }

    @Test
    void getAllTasks_Success() {
        Task task1 = new Task();
        Task task2 = new Task();
        when(taskRepository.findAll()).thenReturn(List.of(task1, task2));

        List<Task> result = taskService.getAllTasks();

        assertEquals(2, result.size());
    }

    @Test
    void getAllTasksByCourse_Success() {
        Task task1 = new Task();
        Task task2 = new Task();
        when(taskRepository.findByCourse("Math")).thenReturn(List.of(task1, task2));

        List<Task> result = taskService.getAllTasksByCourse("Math");

        assertEquals(2, result.size());
    }

    @Test
    void deleteTask_Success() {
        Task task = new Task();
        task.setTitle("Task 1");

        when(taskRepository.findByTitle("Task 1", "Math")).thenReturn(Optional.of(task));

        taskService.deleteTask("Math", "Task 1");

        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void createAnswer_Success() {
        Task task = new Task();
        task.setTitle("Task 1");
        Student student = new Student();
        student.setUsername("student1");
        StudentCourse studentCourse = new StudentCourse();

        when(registerService.myUsername()).thenReturn("student1");
        when(taskRepository.findByTitle("Task 1", "Math")).thenReturn(Optional.of(task));
        when(studentRepository.findByUsername("student1")).thenReturn(Optional.of(student));
        when(studentCourseRepository.findByStudentAndCourse("student1", "Math"))
                .thenReturn(Optional.of(studentCourse));

        taskService.createAnswer("Task 1", "My answer", "Math");

        verify(studentAnswerRepository, times(1)).save(any(StudentAnswer.class));
    }

    @Test
    void getStudentAnswers_Success() {
        StudentAnswer answer = new StudentAnswer();
        when(studentAnswerRepository.uncheckedAnswer("Math")).thenReturn(List.of(answer));

        List<StudentAnswer> result = taskService.getStudentAnswers("Math");

        assertEquals(1, result.size());
    }

    @Test
    void getAllStudentAnswers_Success() {
        StudentAnswer answer = new StudentAnswer();
        when(studentAnswerRepository.getStudentAnswerByStudent("student1", "Math"))
                .thenReturn(List.of(answer));

        List<StudentAnswer> result = taskService.getAllStudentAnswers("student1", "Math");

        assertEquals(1, result.size());
    }

    @Test
    void checkedAnswer_Success() {
        StudentAnswer answer = new StudentAnswer();
        when(studentAnswerRepository.getTaskByTitle("Task 1", "student1", "Math"))
                .thenReturn(Optional.of(answer));

        taskService.checkedAnswer("Task 1", "student1", 5, "Math");

        verify(studentAnswerRepository, times(1)).save(answer);
    }
}
