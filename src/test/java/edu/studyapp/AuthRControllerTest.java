package edu.studyapp;

import edu.studyapp.config.JwtService;
import edu.studyapp.controller.AuthRController;
import edu.studyapp.dto.*;
import edu.studyapp.model.Student;
import edu.studyapp.model.Teacher;
import edu.studyapp.model.User;
import edu.studyapp.service.RegisterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthRControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RegisterService registerService;

    @InjectMocks
    private AuthRController authRController;

    @Test
    void login_Success() {
        AuthRequest request = new AuthRequest("user1", "password");
        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(jwtService.generateToken(auth)).thenReturn("jwt-token");
        when(jwtService.getEXPIRATION_TIME()).thenReturn(3600000L);

        ResponseEntity<?> response = authRController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(AuthResponse.class, response.getBody());
    }

    @Test
    void login_BadCredentials() {
        AuthRequest request = new AuthRequest("user1", "wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        ResponseEntity<?> response = authRController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());
    }

    @Test
    void registerStudent_Success() {
        RegisterStudentRequest request = new RegisterStudentRequest(
                "student1", "password", "John", "Doe", "Group-A");

        Student student = new Student();
        student.setUsername("student1");

        doNothing().when(registerService).registerStudent(request);
        when(registerService.getUserByUsername("student1")).thenReturn(student);

        ResponseEntity<?> response = authRController.addStudent(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(ApiResponse.class, response.getBody());
    }

    @Test
    void registerTeacher_Success() {
        RegisterTeacherRequest request = new RegisterTeacherRequest(
                "teacher1",
                "test",
                "_",
                "-",
                "-");

        Teacher teacher = new Teacher();
        teacher.setUsername("teacher1");

        doNothing().when(registerService).registerTeacher(request);
        when(registerService.getUserByUsername("teacher1")).thenReturn(teacher);

        ResponseEntity<?> response = authRController.addTeacher(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(ApiResponse.class, response.getBody());
    }

    @Test
    void updateProfile_Success() {
        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "NewName",
                "NewLastName",
                "NewGroup",
                null);

        Student student = new Student();
        student.setUsername("student1");

        when(registerService.updateProfile(request)).thenReturn(student);

        ResponseEntity<?> response = authRController.updateProfile(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(ApiResponse.class, response.getBody());
    }
}

