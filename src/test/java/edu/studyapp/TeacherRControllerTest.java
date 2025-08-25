package edu.studyapp;

import edu.studyapp.controller.TeacherRController;
import edu.studyapp.dto.*;
import edu.studyapp.model.*;
import edu.studyapp.service.*;
import edu.studyapp.service.factory.RatingStrategy;
import edu.studyapp.service.factory.RatingStrategyFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherRControllerTest {

    @Mock
    private RatingStrategyFactory strategyFactory;

    @Mock
    private TaskService taskService;

    @Mock
    private LiteratureService literatureService;

    @Mock
    private CourseFileService courseFileService;

    @Mock
    private CourseService courseService;

    @Mock
    private RegisterService registerService;

    @InjectMocks
    private TeacherRController teacherRController;

    @Test
    void listCourses_Success() {
        Teacher teacher = new Teacher();
        Course course = new Course();
        course.setAuthor(teacher);
        when(registerService.myUsername()).thenReturn("teacher1");
        when(courseService.getAllCoursesByTeacher("teacher1")).thenReturn(List.of(course));

        ResponseEntity<?> response = teacherRController.listCourses();

        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(ApiResponse.class, response.getBody());
    }

    @Test
    void addCourse_Success() {
        Teacher teacher = new Teacher();
        Course course = new Course();
        course.setAuthor(teacher);
        CourseRequest request = new CourseRequest("Math",
                "-",
                " ");

        doNothing().when(courseService).createCourse(request);
        when(registerService.myUsername()).thenReturn("teacher1");
        when(courseService.getAllCoursesByTeacher("teacher1")).thenReturn(List.of(course));

        ResponseEntity<?> response = teacherRController.addCourse(request);

        assertEquals(200, response.getStatusCodeValue());
        verify(courseService, times(1)).createCourse(request);
    }

    @Test
    void updateCourse_Success() {
        CourseRequest request = new CourseRequest("NewMath",
                "+",
                "-");
        Teacher teacher = new Teacher();
        Course course = new Course();
        course.setAuthor(teacher);

        when(courseService.updateCourse("Math", request)).thenReturn(course);

        ResponseEntity<?> response = teacherRController.updateCourse("Math", request);

        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(ApiResponse.class, response.getBody());
    }

    @Test
    void addTask_Success() {
        Teacher teacher = new Teacher();
        Course course = new Course();
        course.setAuthor(teacher);
        TaskRequest request = new TaskRequest("Task1",
                "Content",
                "Math");
        Task task = new Task();
        task.setCourse(course);
        doNothing().when(taskService).addTask("Math", request);
        when(taskService.getAllTasksByCourse("Math")).thenReturn(List.of(task));


        ResponseEntity<?> response = teacherRController.addTask(request, "Math");


        assertEquals(200, response.getStatusCodeValue());
        verify(taskService, times(1)).addTask("Math", request);
    }

    @Test
    void addLiterature_Success() {
        Teacher teacher = new Teacher();
        Course course = new Course();
        course.setAuthor(teacher);
        LiteratureRequest request = new LiteratureRequest("Book",
                "Author",
                "link",
                "Math");
        Literature literature = new Literature();
        literature.setCourse(course);

        doNothing().when(literatureService).addLiterature("Math", request);
        when(literatureService.getAllLiteraturesByCourse("Math")).thenReturn(List.of(literature));


        ResponseEntity<?> response = teacherRController.addLiterature(request, "Math");

        assertEquals(200, response.getStatusCodeValue());
        verify(literatureService, times(1)).addLiterature("Math", request);
    }

    @Test
    void tasksStudents_Success() {

        StudentAnswer answer = new StudentAnswer();
        when(taskService.getStudentAnswers("Math")).thenReturn(List.of(answer));


        ResponseEntity<?> response = teacherRController.tasksStudents("Math");

        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(ApiResponse.class, response.getBody());
    }

    @Test
    void checkedAnswer_Success() {
        Student student = new Student();
        Teacher teacher = new Teacher();
        Course course = new Course();
        course.setAuthor(teacher);
        Task task = new Task();
        task.setCourse(course);
        StudentAnswer answer = new StudentAnswer();
        answer.setStudent(student);
        answer.setTask(task);
        doNothing().when(taskService).checkedAnswer("Task1", "student1", 5, "Math");
        when(taskService.getAnswer("Task1", "student1", "Math")).thenReturn(answer);

        ResponseEntity<?> response = teacherRController.checkedAnswer("student1", "Task1", 5, "Math");

        assertEquals(200, response.getStatusCodeValue());
        verify(taskService, times(1)).checkedAnswer("Task1", "student1", 5, "Math");
    }

    @Test
    void checkCourseCompletion_Success() {
        Student student = new Student();
        Teacher teacher = new Teacher();
        Course course = new Course();
        course.setAuthor(teacher);
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setCourse(course);
        studentCourse.setStudent(student);
        studentCourse.setStatus(StudentCourse.EnrollmentStatus.ACTIVE);
        when(courseService.checkCourseCompletion("Math", "student1")).thenReturn(true);
        when(courseService.getStudentCourse("Math", "student1")).thenReturn(studentCourse);

        ResponseEntity<?> response = teacherRController.checkCourseCompletion("student1", "Math");


        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(ApiResponse.class, response.getBody());
    }


    @Test
    void deleteCourse_Success() {
        Teacher teacher = new Teacher();
        Course course = new Course();
        course.setAuthor(teacher);
        doNothing().when(courseService).deleteCourse("Math");
        when(registerService.myUsername()).thenReturn("teacher1");
        when(courseService.getAllCoursesByTeacher("teacher1")).thenReturn(List.of(course));

        ResponseEntity<?> response = teacherRController.deleteCourse("Math");

        assertEquals(200, response.getStatusCodeValue());
        verify(courseService, times(1)).deleteCourse("Math");
    }
}
