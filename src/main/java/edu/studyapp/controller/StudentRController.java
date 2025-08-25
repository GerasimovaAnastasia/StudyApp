package edu.studyapp.controller;

import edu.studyapp.dto.*;
import edu.studyapp.model.*;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import edu.studyapp.service.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/student")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('STUDENT')")
public class StudentRController {
    private final CourseService courseService;
    private final RegisterService registerService;
    private final TaskService taskService;
    private final LiteratureService literatureService;
    private final CourseFileService courseFileService;
    private final FileStorageService fileStorageService;
    public StudentRController(CourseService courseService, RegisterService registerService, TaskService taskService, LiteratureService literatureService, CourseFileService courseFileService, FileStorageService fileStorageService) {
        this.courseService = courseService;
        this.registerService = registerService;
        this.taskService = taskService;
        this.literatureService = literatureService;
        this.courseFileService = courseFileService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/courses")
    public ResponseEntity<?> listCourses() {
        List<CourseRequest> courses =  courseService.getAllCourses()
                .stream()
                .map(CourseRequest::of)
                .toList();;
        return ResponseEntity.ok(
                new ApiResponse<>("Список всех курсов:", courses)

        );
    }
    @GetMapping("/courses/myCourses")
    public ResponseEntity<?> myCourses(@RequestParam String filterBy) {
        if(Objects.equals(filterBy, "completed")) {
            List<StudentCourseRequest> coursesCompleted =  courseService.getCoursesByStudent(registerService.myUsername())
                    .stream()
                    .filter(studentCourse ->
                            Objects.equals(studentCourse.getStatus(), StudentCourse.EnrollmentStatus.COMPLETED))
                    .map(StudentCourseRequest::of)
                    .toList();
            return ResponseEntity.ok(
                    new ApiResponse<>("Список завершенных курсов:", coursesCompleted)

            );
        }
        List<StudentCourseRequest> courses =  courseService.getCoursesByStudent(registerService.myUsername())
                .stream()
                .map(StudentCourseRequest::of)
                .toList();
        return ResponseEntity.ok(
                new ApiResponse<>("Список всех курсов:", courses)

        );
    }
    @GetMapping("/courses/{courseTitle}")
    public ResponseEntity<?> getCourse(@PathVariable String courseTitle) {
        Course course = courseService.getCourse(courseTitle);

        return ResponseEntity.ok(
                new ApiResponse<>("Детали курса:", CourseRequest.of(course))

        );
    }
    @GetMapping("/courses/{courseTitle}/choose")
    public ResponseEntity<?> chooseCourse(@PathVariable String courseTitle) {
        courseService.chooseCourse(courseTitle);
        List<StudentCourseRequest> courses =  courseService.getCoursesByStudent(registerService.myUsername())
                .stream()
                .map(StudentCourseRequest::of)
                .toList();
        return ResponseEntity.ok(
                new ApiResponse<>("Список Ваших курсов:", courses)

        );
    }
    @GetMapping("/courses/{courseTitle}/tasks")
    public ResponseEntity<?> listTasks(@PathVariable String courseTitle) {

        List<TaskRequest> tasks = taskService.getAllTasksByCourse(courseTitle)
                .stream()
                .map(TaskRequest::of)
                .toList();
        return ResponseEntity.ok(
                new ApiResponse<>("Список задач курса:", tasks)

        );
    }
    @GetMapping("/courses/{courseTitle}/tasks/{taskTitle}")
    public ResponseEntity<?> getTask(@PathVariable String courseTitle,
                                     @PathVariable String taskTitle) {

        Task task = taskService.getTask(taskTitle, courseTitle);
        return ResponseEntity.ok(
                new ApiResponse<>("Задача:", TaskRequest.of(task))

        );
    }

    @PostMapping("/courses/{courseTitle}/tasks/{taskTitle}/addAnswer")
    public ResponseEntity<?> addAnswer(@RequestBody StudentAnswerRequest request,
                                       @PathVariable String courseTitle,
                                       @PathVariable String taskTitle) {
        taskService.createAnswer(taskTitle, request.text(), courseTitle);
        List<StudentAnswerRequest> answers = taskService.getAllStudentAnswers(registerService.myUsername(),
                        courseTitle)
                .stream()
                .map(StudentAnswerRequest::of)
                .toList();
        return ResponseEntity.ok(
                new ApiResponse<>("Список Ваших ответов:", answers)

        );
    }
    @GetMapping("/courses/{courseTitle}/tasks/myAnswers")
    public ResponseEntity<?> listMyAnswers(@PathVariable String courseTitle) {

        List<StudentAnswerRequest> answers = taskService.getAllStudentAnswers(registerService.myUsername(),
                        courseTitle)
                .stream()
                .map(StudentAnswerRequest::of)
                .toList();
        return ResponseEntity.ok(
                new ApiResponse<>("Список Ваших ответов:", answers)

        );
    }
    @GetMapping("/courses/{courseTitle}/books")
    public ResponseEntity<?> listBooks(@PathVariable String courseTitle) {

        List<LiteratureRequest> books = literatureService.getAllLiteraturesByCourse(courseTitle)
                .stream()
                .map(LiteratureRequest::of)
                .toList();
        return ResponseEntity.ok(
                new ApiResponse<>("Список литературы курса:", books)

        );
    }
    @GetMapping("/courses/{courseTitle}/files")
    public ResponseEntity<?> listFiles(@PathVariable String courseTitle) {

        List<CourseFileRequest> files = courseFileService.getAllCourseFilesByCourse(courseTitle)
                .stream()
                .map(CourseFileRequest::of)
                .toList();
        return ResponseEntity.ok(
                new ApiResponse<>("Список файлов курса:", files)

        );
    }
    @GetMapping("/courses/{courseTitle}/files/{titleFile}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable String titleFile) {

        CourseFile file = courseFileService.getCourseFile(titleFile);
        String filePath = file.getFilePath();
        Resource resource = fileStorageService.loadFileAsResource(filePath);

        String contentType = fileStorageService.determineContentType(resource);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    @GetMapping("/courses/{courseTitle}/books/{titleBooks}/download")
    public ResponseEntity<Resource> downloadBook(@PathVariable String titleBooks) {

        Literature book = literatureService.getLiterature(titleBooks);
        String bookPath = book.getLink();
        Resource resource = fileStorageService.loadFileAsResource(bookPath);

        String contentType = fileStorageService.determineContentType(resource);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }



}
