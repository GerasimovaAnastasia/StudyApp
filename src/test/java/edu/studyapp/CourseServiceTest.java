package edu.studyapp;

import edu.studyapp.dto.CourseRequest;
import edu.studyapp.model.*;
import edu.studyapp.repository.*;
import edu.studyapp.service.CourseService;
import edu.studyapp.service.RegisterService;
import edu.studyapp.service.TaskService;
import edu.studyapp.service.exception.EntityNotFoundException;
import edu.studyapp.service.exception.ValidateHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private RegisterService registerService;

    @Mock
    private TaskService taskService;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentCourseRepository studentCourseRepository;

    @Mock
    private ValidateHandler validateHandler;

    @InjectMocks
    private CourseService courseService;

    @Test
    void createCourse_Success() {

        CourseRequest request = new CourseRequest("Math", "Mathematics course", "-");
        Teacher teacher = new Teacher();
        teacher.setUsername("teacher1");

        when(registerService.myUsername()).thenReturn("teacher1");
        when(teacherRepository.findByUsername("teacher1")).thenReturn(Optional.of(teacher));

        courseService.createCourse(request);

        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void getCourse_Success() {

        Course course = new Course();
        course.setTitle("Math");
        when(courseRepository.findByTitle("Math")).thenReturn(Optional.of(course));

        Course result = courseService.getCourse("Math");

        assertNotNull(result);
        assertEquals("Math", result.getTitle());
    }

    @Test
    void getCourse_NotFound_ThrowsException() {

        when(courseRepository.findByTitle("Unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            courseService.getCourse("Unknown");
        });
    }

    @Test
    void getAllCourses_Success() {

        Course course1 = new Course();
        Course course2 = new Course();
        when(courseRepository.findAll()).thenReturn(List.of(course1, course2));

        List<Course> result = courseService.getAllCourses();

        assertEquals(2, result.size());
    }

    @Test
    void updateCourse_Success() {

        Course course = new Course();
        course.setTitle("OldTitle");
        CourseRequest request = new CourseRequest("NewTitle",
                "New description", "-");

        when(courseRepository.findByTitle("OldTitle")).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course result = courseService.updateCourse("OldTitle", request);

        assertNotNull(result);
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void deleteCourse_Success() {

        Course course = new Course();
        course.setTitle("Math");
        when(courseRepository.findByTitle("Math")).thenReturn(Optional.of(course));

        courseService.deleteCourse("Math");

        verify(courseRepository, times(1)).delete(course);
    }

    @Test
    void chooseCourse_Success() {

        Course course = new Course();
        course.setTitle("Math");
        Student student = new Student();
        student.setUsername("student1");

        when(registerService.myUsername()).thenReturn("student1");
        when(courseRepository.findByTitle("Math")).thenReturn(Optional.of(course));
        when(studentRepository.findByUsername("student1")).thenReturn(Optional.of(student));

        courseService.chooseCourse("Math");

        verify(studentCourseRepository, times(1)).save(any(StudentCourse.class));
    }

    @Test
    void checkCourseCompletion_AllTasksCompleted() {

        Course course = new Course();

        Task task1 = new Task();
        Task task2 = new Task();
        when(taskService.getAllTasksByCourse("Math")).thenReturn(List.of(task1, task2));

        StudentAnswer answer1 = new StudentAnswer();
        answer1.setGrade(5);
        StudentAnswer answer2 = new StudentAnswer();
        answer2.setGrade(4);
        when(taskService.getAllStudentAnswers("student1", "Math")).thenReturn(List.of(answer1, answer2));

        boolean result = courseService.checkCourseCompletion("Math", "student1");

        assertTrue(result);
    }


    @Test
    void updateCourseStatus_Success() {

        Student student = new Student();
        student.setUsername("student1");
        Course course = new Course();
        course.setTitle("Math");
        StudentCourse studentCourse = new StudentCourse();

        when(studentRepository.findByUsername("student1")).thenReturn(Optional.of(student));
        when(courseRepository.findByTitle("Math")).thenReturn(Optional.of(course));
        when(studentCourseRepository.findByStudentAndCourse("student1", "Math"))
                .thenReturn(Optional.of(studentCourse));

        courseService.updateCourseStatus("Math", "student1", StudentCourse.EnrollmentStatus.COMPLETED);

        verify(studentCourseRepository, times(1)).save(studentCourse);
    }

    @Test
    void getStudentsByCourse_Success() {

        StudentCourse sc1 = new StudentCourse();
        StudentCourse sc2 = new StudentCourse();
        when(studentCourseRepository.findByCourse("Math")).thenReturn(List.of(sc1, sc2));

        List<StudentCourse> result = courseService.getStudentsByCourse("Math");

        assertEquals(2, result.size());
    }
}
