package edu.studyapp.service;

import edu.studyapp.dto.CourseRequest;
import edu.studyapp.model.*;
import edu.studyapp.repository.*;
import edu.studyapp.service.exception.EntityNotFoundException;
import edu.studyapp.service.exception.ValidateHandler;
//import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final RegisterService registerService;
    private final TaskService taskService;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final StudentCourseRepository studentCourseRepository;
    private final ValidateHandler validateHandler;
    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    public CourseService(CourseRepository courseRepository, RegisterService registerService, TaskService taskService, TeacherRepository teacherRepository, StudentRepository studentRepository, StudentCourseRepository studentCourseRepository, ValidateHandler validateHandler) {
        this.courseRepository = courseRepository;
        this.registerService = registerService;
        this.taskService = taskService;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.studentCourseRepository = studentCourseRepository;
        this.validateHandler = validateHandler;
    }

    public void createCourse (CourseRequest request) {
        logger.info("Создание курса: {}", request.title());
        Objects.requireNonNull(request);
        Teacher teacher = teacherRepository.findByUsername(registerService.myUsername())
                .orElseThrow(() -> {
                    logger.error("Преподаватель не найден.");
                    String errorMessage = String.format("Преподаватель не найден: %s", registerService.myUsername());
                    return new EntityNotFoundException(errorMessage);
                });

        Course course = Course.builder()
                .title(request.title())
                .description(request.description())
                .author(teacher)
                .build();
        courseRepository.save(course);
    }
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourse (String courseTitle) {
        validateHandler.validateStringNotBlank(courseTitle);
        return courseRepository.findByTitle(courseTitle)
                .orElseThrow(() -> {
                    logger.error("Курс не найден.");
                    String errorMessage = String.format("Курс не найден: %s", courseTitle);
                    return new EntityNotFoundException(errorMessage);
                });
    }
    public List<Course> getAllCoursesByTeacher(String username) {
        return courseRepository.findByTeacher(username);
    }
    @Transactional
    public Course updateCourse(String courseTitle, CourseRequest request) {
        logger.info("Обновление курса: {}", request.title());
        Objects.requireNonNull(request);
        Course course = getCourse(courseTitle);
        course.setTitle(request.title());
        course.setDescription(request.description());


        return courseRepository.save(course);
    }
    public void deleteCourse(String courseTitle) {
        logger.info("Удаление курса: {}", courseTitle);
        validateHandler.validateStringNotBlank(courseTitle);
        courseRepository.delete(getCourse(courseTitle));
    }

    public void chooseCourse (String courseTitle) {
        logger.info("Выбор курса {} студентом {}.", courseTitle, registerService.myUsername());
        validateHandler.validateStringNotBlank(courseTitle);

        StudentCourse studentCourse = StudentCourse.builder()
                .student(studentRepository.findByUsername(registerService.myUsername()).orElseThrow())
                .course(getCourse(courseTitle))
                .status(StudentCourse.EnrollmentStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .build();
        studentCourseRepository.save(studentCourse);

    }

    public List<StudentCourse> getCoursesByStudent(String usernameStudent) {
        validateHandler.validateStringNotBlank(usernameStudent);
        return studentCourseRepository.findByStudent(usernameStudent);
    }
    public boolean checkCourseCompletion (String courseTitle, String usernameStudent) {
        logger.info("Проверка статуса курса: {} у студента: {}", courseTitle, usernameStudent);
        List<Task> tasks = Optional.ofNullable(taskService.getAllTasksByCourse(courseTitle))
                .orElse(Collections.emptyList());

        List<StudentAnswer> answers = Optional.ofNullable(
                        taskService.getAllStudentAnswers(usernameStudent, courseTitle))
                .orElse(Collections.emptyList());
        if (tasks.isEmpty() && getCourse(courseTitle) == null) {
            logger.error("Курс не найден в checkCourseCompletion()");
            throw new EntityNotFoundException("Курс не найден.");
        }
        int numberTask = tasks.size();
        int numberAnswer = answers.size();

        boolean hasBadGrades = answers.stream()
                .anyMatch(answer -> answer.getGrade() == null || answer.getGrade() < 3);

        //StudentCourse course = studentCourseRepository.findByStudentAndCourse(usernameStudent, courseTitle);
        //            course.setStatus(StudentCourse.EnrollmentStatus.COMPLETED);
        //            studentCourseRepository.save(course);
        return numberTask == numberAnswer && !hasBadGrades;
    }
    public void updateCourseStatus (String courseTitle, String username,
                                    StudentCourse.EnrollmentStatus status) {
        logger.info("Изменение статуса курса: {} у студента: {}", courseTitle, username);
        validateHandler.validateStringNotBlank(courseTitle);
        validateHandler.validateStringNotBlank(username);

        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Студент не найден в updateCourseStatus()");
                    String errorMessage = String.format("Студент не найден: %s", username);
                    return new EntityNotFoundException(errorMessage);
                });
        Course course = getCourse(courseTitle);

        StudentCourse studentCourse = getStudentCourse(course.getTitle(), student.getUsername());
        studentCourse.setStatus(status);

        studentCourseRepository.save(studentCourse);
    }
    public StudentCourse getStudentCourse (String courseTitle, String username) {
        validateHandler.validateStringNotBlank(courseTitle);
        validateHandler.validateStringNotBlank(username);

        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Студент не найден в getStudentCourse()");
                    String errorMessage = String.format("Студент не найден: %s", username);
                    return new EntityNotFoundException(errorMessage);
                });
        Course course = getCourse(courseTitle);
        return studentCourseRepository.findByStudentAndCourse(
                        student.getUsername(), course.getTitle())
                .orElseThrow(()-> {
                    logger.error("Запись о курсе у студента не найдено в getStudentCourse()");
                    String errorMessage = String.format("Запись о курсе %s у студента %s не найдено.", courseTitle, username);
                    return new EntityNotFoundException(errorMessage);
                });
    }
    public List<StudentCourse> getStudentsByCourse(String courseTitle) {
        validateHandler.validateStringNotBlank(courseTitle);
        return studentCourseRepository.findByCourse(courseTitle);
    }
    public List<StudentCourse> getStudentsByCourseStatus(String courseTitle) {
        validateHandler.validateStringNotBlank(courseTitle);
        return studentCourseRepository.findByStatus(courseTitle);
    }


}
