package edu.studyapp.service;

import edu.studyapp.config.JwtService;
import edu.studyapp.dto.*;
import edu.studyapp.model.Student;
import edu.studyapp.model.Teacher;
import edu.studyapp.model.User;
import edu.studyapp.repository.StudentRepository;
import edu.studyapp.repository.TeacherRepository;
import edu.studyapp.repository.UserRepository;

import edu.studyapp.service.exception.EntityNotFoundException;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.w3c.dom.events.EventException;

import java.util.Objects;


@Service
public class RegisterService implements UserDetailsService {
    //@Autowired
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

    public RegisterService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;

        this.userRepository = userRepository;
    }
    @Transactional
    public void registerStudent(RegisterStudentRequest request) {

        Objects.requireNonNull(request);
        if (userRepository.findByUsername(request.username()).isPresent()) {
            String errorMessage = String.format("Username '%s' уже существует", request.username());
            logger.warn("{}: {}", "registerStudent", errorMessage);
            throw new EntityExistsException("Username уже существует");
        }
        Student student = Student.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role("ROLE_STUDENT")
                .firstName(request.firstName())
                .lastName(request.lastName())
                .groupName(request.groupName())
                .build();
        userRepository.save(student);
    }
    @Transactional
    public void registerTeacher(RegisterTeacherRequest request) {

        Objects.requireNonNull(request);
        if (userRepository.findByUsername(request.username()).isPresent()) {
            String errorMessage = String.format("Username '%s' уже существует", request.username());
            logger.warn("{}: {}", "registerTeacher", errorMessage);
            throw new EntityExistsException("Username уже существует");
        }
        Teacher teacher = Teacher.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role("ROLE_TEACHER")
                .firstName(request.firstName())
                .lastName(request.lastName())
                .position(request.position())
                .build();
        userRepository.save(teacher);
    }

    @Transactional
    public User updateProfile(ProfileUpdateRequest updateRequest) {
        Objects.requireNonNull(updateRequest);
        User user = userRepository.findByUsername(myUsername())
                .orElseThrow(() -> {
                    String errorMessage = "User не найден";
                    logger.warn("{}: {}", "updateProfile", errorMessage);
                    return new EntityNotFoundException("User не найден");
                });

        user.setFirstName(updateRequest.firstName());
        user.setLastName(updateRequest.lastName());


        if (user instanceof Student student && updateRequest.groupName() != null) {
            student.setGroupName(updateRequest.groupName());
            logger.info("Студент %s изменил группу: {}", user.getUsername());
        }

        if (user instanceof Teacher teacher && updateRequest.position() != null) {
            teacher.setPosition(updateRequest.position());
            logger.info("Преподаватель %s изменил должность: {}", user.getUsername());
        }

        return userRepository.save(user);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Пользователь не найден: %s", username);
                    logger.warn("{}: {}", "getUserByUsername", errorMessage);
                    return new EntityNotFoundException(errorMessage);
                });
    }
    public String myUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getName();
    }
}
