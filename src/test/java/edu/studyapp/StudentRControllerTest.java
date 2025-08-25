package edu.studyapp;

import edu.studyapp.controller.StudentRController;
import edu.studyapp.dto.*;
import edu.studyapp.model.*;
import edu.studyapp.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentRControllerTest {

    @Mock
    private CourseService courseService;

    @Mock
    private RegisterService registerService;

    @Mock
    private TaskService taskService;

    @Mock
    private LiteratureService literatureService;

    @Mock
    private CourseFileService courseFileService;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private StudentRController studentRController;

    @Test
    void listCourses_Success() {
        Teacher teacher = new Teacher();
        Course course = new Course();
        course.setTitle("Title");
        course.setDescription("-");
        course.setAuthor(teacher);
        when(courseService.getAllCourses()).thenReturn(List.of(course));

        ResponseEntity<?> response = studentRController.listCourses();

        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(ApiResponse.class, response.getBody());
    }


    @Test
    void getCourse_Success() {
        Teacher teacher = new Teacher();
        Course course = new Course();
        course.setAuthor(teacher);
        when(courseService.getCourse("Math")).thenReturn(course);

        ResponseEntity<?> response = studentRController.getCourse("Math");

        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(ApiResponse.class, response.getBody());
    }

    @Test
    void chooseCourse_Success() {
        Student student = new Student();
        Course course = new Course();
        course.setTitle("Title");
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudent(student);
        studentCourse.setCourse(course);
        studentCourse.setStatus(StudentCourse.EnrollmentStatus.ACTIVE);
        when(registerService.myUsername()).thenReturn("student1");
        doNothing().when(courseService).chooseCourse("Math");
        when(courseService.getCoursesByStudent("student1")).thenReturn(List.of(studentCourse));

        ResponseEntity<?> response = studentRController.chooseCourse("Math");

        assertEquals(200, response.getStatusCodeValue());
        verify(courseService, times(1)).chooseCourse("Math");
    }

    @Test
    void listTasks_Success() {
        Course course = new Course();
        course.setTitle("Title");
        Task task = new Task();
        task.setCourse(course);
        when(taskService.getAllTasksByCourse("Math")).thenReturn(List.of(task));

        ResponseEntity<?> response = studentRController.listTasks("Math");

        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(ApiResponse.class, response.getBody());
    }

    @Test
    void getTask_Success() {
        Course course = new Course();
        course.setTitle("Title");
        Task task = new Task();
        task.setCourse(course);
        when(taskService.getTask("Task1", "Math")).thenReturn(task);

        ResponseEntity<?> response = studentRController.getTask("Math", "Task1");

        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(ApiResponse.class, response.getBody());
    }

    @Test
    void addAnswer_Success() {
        Student student = new Student();
        Course course = new Course();
        course.setTitle("Math");
        Task task = new Task();
        task.setCourse(course);
        StudentAnswerRequest request = new StudentAnswerRequest("student1",
                "Task1",
                "My answer",
                4);
        StudentAnswer answer = new StudentAnswer();
        answer.setTask(task);
        answer.setStudent(student);
        when(registerService.myUsername()).thenReturn("student1");
        doNothing().when(taskService).createAnswer("Task1", "My answer", "Math");
        when(taskService.getAllStudentAnswers("student1", "Math")).thenReturn(List.of(answer));

        ResponseEntity<?> response = studentRController.addAnswer(request, "Math", "Task1");

        assertEquals(200, response.getStatusCodeValue());
        verify(taskService, times(1)).createAnswer("Task1", "My answer", "Math");
    }

    @Test
    void listMyAnswers_Success() {
        Course course = new Course();
        course.setTitle("Math");
        Student student = new Student();
        Task task = new Task();
        task.setCourse(course);
        StudentAnswer answer = new StudentAnswer();
        answer.setStudent(student);
        answer.setTask(task);
        when(registerService.myUsername()).thenReturn("student1");
        when(taskService.getAllStudentAnswers("student1", "Math")).thenReturn(List.of(answer));

        ResponseEntity<?> response = studentRController.listMyAnswers("Math");

        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(ApiResponse.class, response.getBody());
    }

    @Test
    void listBooks_Success() {
        Course course = new Course();
        course.setTitle("Math");
        Literature literature = new Literature();
        literature.setCourse(course);

        when(literatureService.getAllLiteraturesByCourse("Math")).thenReturn(List.of(literature));

        ResponseEntity<?> response = studentRController.listBooks("Math");

        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(ApiResponse.class, response.getBody());
    }

    @Test
    void listFiles_Success() {
        Course course = new Course();
        course.setTitle("Math");
        CourseFile courseFile = new CourseFile();
        courseFile.setCourse(course);
        when(courseFileService.getAllCourseFilesByCourse("Math")).thenReturn(List.of(courseFile));

        ResponseEntity<?> response = studentRController.listFiles("Math");

        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(ApiResponse.class, response.getBody());
    }
}