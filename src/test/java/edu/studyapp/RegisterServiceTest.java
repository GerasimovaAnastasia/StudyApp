package edu.studyapp;

import edu.studyapp.dto.RegisterStudentRequest;
import edu.studyapp.dto.RegisterTeacherRequest;
import edu.studyapp.model.Student;
import edu.studyapp.model.Teacher;
import edu.studyapp.model.User;
import edu.studyapp.repository.UserRepository;
import edu.studyapp.service.RegisterService;
import edu.studyapp.service.exception.EntityNotFoundException;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private RegisterService registerService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void registerStudent_Success() {

        RegisterStudentRequest request = new RegisterStudentRequest(
                "student1",
                "test",
                "Иван",
                "Иванов",
                "A"
        );

        when(userRepository.findByUsername("student1")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("test")).thenReturn("encodedPassword");


        registerService.registerStudent(request);


        verify(userRepository, times(1)).save(any(Student.class));
    }
    @Test
    void registerTeacher_Success() {

        RegisterTeacherRequest request = new RegisterTeacherRequest(
                "teacher1",
                "test",
                "Иван",
                "Иванов",
                "A"
        );

        when(userRepository.findByUsername("teacher1")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("test")).thenReturn("encodedPassword");


        registerService.registerTeacher(request);


        verify(userRepository, times(1)).save(any(Teacher.class));
    }
    @Test
    void registerStudent_UsernameExists() {

        RegisterStudentRequest request = new RegisterStudentRequest(
                "username1",
                "test",
                "Иван",
                "Иванов",
                "A"
        );

        Student existingStudent = new Student();
        when(userRepository.findByUsername("username1")).thenReturn(Optional.of(existingStudent));

        assertThrows(EntityExistsException.class, () -> {
            registerService.registerStudent(request);
        });
    }
    @Test
    void getUserByUsername_UserExists() {

        Student student = new Student();
        student.setUsername("username");

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(student));

        User result = registerService.getUserByUsername("username");


        assertNotNull(result);
        assertEquals("username", result.getUsername());
    }

    @Test
    void getUserByUsername_UserNotFound() {

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            registerService.getUserByUsername("unknown");
        });
    }

}
