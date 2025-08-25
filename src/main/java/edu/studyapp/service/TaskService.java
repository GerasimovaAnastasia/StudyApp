package edu.studyapp.service;

import edu.studyapp.dto.TaskRequest;
import edu.studyapp.model.Course;
import edu.studyapp.model.Student;
import edu.studyapp.model.StudentAnswer;
import edu.studyapp.model.Task;
import edu.studyapp.repository.*;
import edu.studyapp.service.exception.EntityNotFoundException;
import edu.studyapp.service.exception.ValidateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;
    private final RegisterService registerService;
    private final StudentRepository studentRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final ValidateHandler validateHandler;
    private final StudentCourseRepository studentCourseRepository;
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    public TaskService(TaskRepository taskRepository, CourseRepository courseRepository, RegisterService registerService, StudentRepository studentRepository, StudentAnswerRepository studentAnswerRepository, ValidateHandler validateHandler, StudentCourseRepository studentCourseRepository) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
        this.registerService = registerService;
        this.studentRepository = studentRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.validateHandler = validateHandler;
        this.studentCourseRepository = studentCourseRepository;
    }

    public void addTask(String courseTitle, TaskRequest request) {
        Objects.requireNonNull(courseTitle);
        logger.info("Добавление задачи {} для курса: {}", request.title(),courseTitle);
        Task task = Task.builder()
                .taskContent(request.taskContent())
                .title(request.title())
                .course(courseRepository.findByTitle(courseTitle).orElseThrow())
                .build();
        taskRepository.save(task);
    }
    public Task getTask(String title, String courseTitle) {
        validateHandler.validateStringNotBlank(title);
        validateHandler.validateStringNotBlank(courseTitle);
        return taskRepository.findByTitle(title, courseTitle).orElseThrow(() -> {
            String errorMessage = String.format("Задача не найдена: %s", title);
            logger.warn("{}: {}", "getTask", errorMessage);
            return new EntityNotFoundException(errorMessage);
        });
    }
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    public List<Task> getAllTasksByCourse(String courseName) {
        validateHandler.validateStringNotBlank(courseName);
        return taskRepository.findByCourse(courseName);
    }
    public void deleteTask(String courseTitle, String title) {
        validateHandler.validateStringNotBlank(courseTitle);
        validateHandler.validateStringNotBlank(title);
        logger.info("Удаление задачи {} из курса: {}", title, courseTitle);
        taskRepository.delete(getTask(title, courseTitle));
    }
    public void createAnswer (String taskTitle, String answer, String courseTitle) {

        validateHandler.validateStringNotBlank(taskTitle);
        validateHandler.validateStringNotBlank(answer);
        validateHandler.validateStringNotBlank(courseTitle);
        logger.info("Добавление ответа на задачу {} в курсе: {}", taskTitle, courseTitle);
        Task task = getTask(taskTitle, courseTitle);

        Student student = studentRepository.findByUsername(registerService.myUsername())
                .orElseThrow(() -> {
                    String errorMessage = String.format( "Студент не найден: " + registerService.myUsername());
                    logger.warn("{}: {}", "createAnswer", errorMessage);
                    return new EntityNotFoundException(errorMessage);
                });

        if (studentCourseRepository.findByStudentAndCourse(student.getUsername(), courseTitle).isEmpty()) {
            String errorMessage = "Студент не может добавить ответ на задание курса, который он не проходит.";
            logger.warn("{}: {}", "createAnswer", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        if (!studentAnswerRepository.getStudentAnswerByStudent(student.getUsername(), courseTitle).isEmpty()) {
            String errorMessage = "Студент не может добавить ответ на задание курса, на которое уже был дан ответ.";
            logger.warn("{}: {}", "createAnswer", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        StudentAnswer studentAnswer = StudentAnswer.builder()
                .student(student)
                .answerText(answer)
                .task(task)
                .grade(null)
                .build();
        studentAnswerRepository.save(studentAnswer);
    }
    public List<StudentAnswer> getStudentAnswers(String courseTitle) {
        validateHandler.validateStringNotBlank(courseTitle);
        return studentAnswerRepository.uncheckedAnswer(courseTitle);
    }
    public void checkedAnswer(String taskTitle, String username, Integer grade, String courseTitle) {
        validateHandler.validateStringNotBlank(courseTitle);
        validateHandler.validateStringNotBlank(taskTitle);
        validateHandler.validateStringNotBlank(username);
        logger.info("Проверка ответа на задачу {} в курсе: {}", taskTitle, courseTitle);

        StudentAnswer studentAnswer = getAnswer(taskTitle, username, courseTitle);

       studentAnswer.setGrade(grade);
       studentAnswerRepository.save(studentAnswer);
    }
    public StudentAnswer getAnswer(String taskTitle, String username, String courseTitle) {
        return studentAnswerRepository.getTaskByTitle(taskTitle, username, courseTitle)
                .orElseThrow(() -> {
                    String errorMessage = String.format( "Запись ответа студента %s по задаче %d не найдена.", username, taskTitle);
                    logger.warn("{}: {}", "getAnswer", errorMessage);
                    return new EntityNotFoundException(errorMessage);
                });
    }
    public List<StudentAnswer> getAllStudentAnswers(String username, String courseTitle) {
        validateHandler.validateStringNotBlank(courseTitle);
        validateHandler.validateStringNotBlank(username);
        return studentAnswerRepository.getStudentAnswerByStudent(username, courseTitle);
    }
}
