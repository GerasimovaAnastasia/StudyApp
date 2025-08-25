package edu.studyapp.controller;

import edu.studyapp.config.JwtAuthenticationFilter;
import edu.studyapp.config.JwtService;
import edu.studyapp.dto.*;
import edu.studyapp.model.Teacher;
import edu.studyapp.model.User;
import edu.studyapp.repository.StudentRepository;
import edu.studyapp.repository.TeacherRepository;
import edu.studyapp.service.RegisterService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@SecurityRequirement(name = "bearerAuth")
public class AuthRController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RegisterService registerService;
    private static final Logger logger = LoggerFactory.getLogger(AuthRController.class);

    public AuthRController(AuthenticationManager authenticationManager, JwtService jwtService, RegisterService registerService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.registerService = registerService;
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));

            String token = jwtService.generateToken(auth);
            return ResponseEntity.ok(new AuthResponse(token, "Bearer", jwtService.getEXPIRATION_TIME()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(ErrorResponse.of(401, "Неверный логин или пароль."));
        }
    }
    @PostMapping("/registerStudent")
    public ResponseEntity<?> addStudent(@Valid @RequestBody RegisterStudentRequest request) {
        try {
            registerService.registerStudent(request);
            return ResponseEntity.ok(
                    new ApiResponse<>("Регистрация студента", UserResponse.of(registerService.getUserByUsername(request.username()))
                    )
            );
        } catch (Exception e) {
            logger.error("Ошибка регистрации студента {}: {}",
                    request.username(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Ошибка студента", null));
        }

    }
    @PostMapping("/registerTeacher")
    public ResponseEntity<?> addTeacher(@Valid @RequestBody RegisterTeacherRequest request) {
        try {
            registerService.registerTeacher(request);

            return ResponseEntity.ok(
                    new ApiResponse<>("Регистрация преподавателя",  UserResponse.of(registerService.getUserByUsername(request.username()))
                    )
            );
        } catch (Exception e) {
            logger.error("Ошибка регистрации преподавателя {}: {}",
                    request.username(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Ошибка регистрации", null));
        }

    }

    @PostMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateRequest request) {

        User user = registerService.updateProfile(request);

        return ResponseEntity.ok(
                new ApiResponse<>("Данные обновлены!",  UserResponse.of(user))

        );
    }



}
