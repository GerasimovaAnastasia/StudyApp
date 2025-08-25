package edu.studyapp.controller;

import edu.studyapp.dto.*;
import edu.studyapp.model.Course;
import edu.studyapp.model.StudentAnswer;
import edu.studyapp.model.StudentCourse;
import edu.studyapp.model.User;
import edu.studyapp.service.*;
import edu.studyapp.service.factory.ProgressRatingStrategy;
import edu.studyapp.service.factory.RatingContext;
import edu.studyapp.service.factory.RatingStrategy;
import edu.studyapp.service.factory.RatingStrategyFactory;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teacher")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherRController {
    private final RatingStrategyFactory strategyFactory;
    private final TaskService taskService;
    private final LiteratureService literatureService;
    private final CourseFileService courseFileService;
    private final CourseService  courseService;
    private final RegisterService registerService;

    public TeacherRController(RatingStrategyFactory strategyFactory, TaskService taskService, LiteratureService literatureService, CourseFileService courseFileService, CourseService courseService, RegisterService registerService) {
        this.strategyFactory = strategyFactory;
        this.taskService = taskService;
        this.literatureService = literatureService;
        this.courseFileService = courseFileService;
        this.courseService = courseService;
        this.registerService = registerService;
    }

    @GetMapping("/courses")
    public ResponseEntity<?> listCourses() {
        List<CourseRequest> courses =  courseService.getAllCoursesByTeacher(registerService.myUsername())
                .stream()
                .map(CourseRequest::of)
                .toList();;
        return ResponseEntity.ok(
                new ApiResponse<>("Список Ваших курсов:", courses)

        );
    }
    @PostMapping("/courses/addCourse")
    public ResponseEntity<?> addCourse(@RequestBody CourseRequest request) {
        courseService.createCourse(request);
        List<CourseRequest> courses = courseService.getAllCoursesByTeacher(registerService.myUsername())
                .stream()
                .map(CourseRequest::of)
                .toList();
        return ResponseEntity.ok(
                new ApiResponse<>("Список Ваших курсов:", courses)

        );
    }
//    @GetMapping("/courses/{courseTitle}")
//    public ResponseEntity<?> getMyCourse(@PathVariable String courseTitle) {
//        Course course = courseService.getCourse(courseTitle);
//
//        return ResponseEntity.ok(
//                new ApiResponse<>("Детали курса:", CourseRequest.of(course))
//
//        );
//    }
    @PutMapping("/courses/{courseTitle}/update")
    public ResponseEntity<?> updateCourse(@PathVariable String courseTitle,
                                          @RequestBody CourseRequest request) {
        Course course = courseService.updateCourse(courseTitle, request);

        return ResponseEntity.ok(
                new ApiResponse<>("Данные курса:", CourseRequest.of(course))

        );
    }
    @PostMapping("/courses/{courseTitle}/update/addTask")
    public ResponseEntity<?> addTask(@RequestBody TaskRequest request, @PathVariable String courseTitle) {
        taskService.addTask(courseTitle, request);
        List<TaskRequest> tasks = taskService.getAllTasksByCourse(courseTitle)
                .stream()
                .map(TaskRequest::of)
                .toList();
        return ResponseEntity.ok(
                new ApiResponse<>("Список задач курса:", tasks)

        );
    }
    @PostMapping("/courses/{courseTitle}/update/addLiterature")
    public ResponseEntity<?> addLiterature(@RequestBody LiteratureRequest request, @PathVariable String courseTitle) {
        literatureService.addLiterature(courseTitle, request);
        List<LiteratureRequest> books = literatureService.getAllLiteraturesByCourse(courseTitle)
                .stream()
                .map(LiteratureRequest::of)
                .toList();
        return ResponseEntity.ok(
                new ApiResponse<>("Список книг курса:", books)

        );
    }
    @PostMapping("/courses/{courseTitle}/update/addFile")
    public ResponseEntity<?> addFile(@RequestBody CourseFileRequest request, @PathVariable String courseTitle) {
        courseFileService.addCourseFile(courseTitle, request);
        List<CourseFileRequest> files = courseFileService.getAllCourseFilesByCourse(courseTitle)
                .stream()
                .map(CourseFileRequest::of)
                .toList();
        return ResponseEntity.ok(
                new ApiResponse<>("Список файлов курса:", files)

        );
    }
    @DeleteMapping("/courses/{courseTitle}/update/delete")
    public ResponseEntity<?> deleteArtefacts(@PathVariable String courseTitle,
                                             @RequestParam String title,
                                             @RequestParam String type) {
        if (type != null) {
            switch (type.toLowerCase()) {
                case "task":
                    taskService.deleteTask(courseTitle, title);
                    List<TaskRequest> tasks = taskService.getAllTasksByCourse(courseTitle)
                            .stream()
                            .map(TaskRequest::of)
                            .toList();
                    return ResponseEntity.ok(
                        new ApiResponse<>("Список задач курса:", tasks)

                );
                case "literature":
                   literatureService.deleteLiterature(title);
                    List<LiteratureRequest> books = literatureService.getAllLiteraturesByCourse(courseTitle)
                            .stream()
                            .map(LiteratureRequest::of)
                            .toList();
                    return ResponseEntity.ok(
                            new ApiResponse<>("Список книг курса:", books)

                    );
                case "file":
                   courseFileService.deleteCourseFile(title);
                    List<CourseFileRequest> files = courseFileService.getAllCourseFilesByCourse(courseTitle)
                            .stream()
                            .map(CourseFileRequest::of)
                            .toList();
                    return ResponseEntity.ok(
                            new ApiResponse<>("Список файлов курса:", files)

                    );
                default:
                    return ResponseEntity.badRequest().body("Недопустимый параметр запроса");
            }
        }
        return ResponseEntity.badRequest().body("Укажите тип данных.");
    }
    @DeleteMapping("/courses/{courseTitle}/delete")
    public ResponseEntity<?> deleteCourse(@PathVariable String courseTitle) {
        courseService.deleteCourse(courseTitle);
        List<CourseRequest> files = courseService.getAllCoursesByTeacher(registerService.myUsername())
                .stream()
                .map(CourseRequest::of)
                .toList();
        return ResponseEntity.ok(
                new ApiResponse<>("Список курсов:", files)

        );
    }
    @GetMapping("/courses/{courseTitle}/tasksStudents")
    public ResponseEntity<?> tasksStudents(@PathVariable String courseTitle) {
        List<StudentAnswer> answers = taskService.getStudentAnswers(courseTitle);

        return ResponseEntity.ok(
                new ApiResponse<>("Список непроверенных ответов студентов:", answers)

        );
    }
    @GetMapping("/courses/{courseTitle}/checkedAnswer")
    public ResponseEntity<?> checkedAnswer(@RequestParam String username,
                                           @RequestParam String taskTitle,
                                           @RequestParam Integer grade,
                                           @PathVariable String courseTitle) {
        taskService.checkedAnswer(taskTitle, username, grade, courseTitle);
        StudentAnswer answer = taskService.getAnswer(taskTitle, username, courseTitle);
        return ResponseEntity.ok(
                new ApiResponse<>("Ответ оценен", StudentAnswerRequest.of(answer))

        );
    }
    @GetMapping("/courses/{courseTitle}/checkCompletion")
    public ResponseEntity<?> checkCourseCompletion(@RequestParam String username,
                                           @PathVariable String courseTitle) {

        boolean isCompleted = courseService.checkCourseCompletion(courseTitle, username);
        String message = isCompleted
                ? "Курс пройден студентом."
                : "Курс не пройден студентом.";
        StudentCourse course = courseService.getStudentCourse(courseTitle, username);
        return ResponseEntity.ok(
                new ApiResponse<>(message, StudentCourseRequest.of(course)));
    }
    @PatchMapping("/courses/{courseTitle}/updateStatus")
    public ResponseEntity<?> updateStatus(@RequestParam String status,
                                          @RequestParam String username,
                                          @PathVariable String courseTitle) {

       switch (status) {
           case "completed": courseService.updateCourseStatus(courseTitle,
                   username, StudentCourse.EnrollmentStatus.COMPLETED);
           break;
           case "active" : courseService.updateCourseStatus(courseTitle,
                   username, StudentCourse.EnrollmentStatus.ACTIVE);
               break;
           default: ResponseEntity.ok("Error!");
       }
        StudentCourse course = courseService.getStudentCourse(courseTitle, username);
        return ResponseEntity.ok(
                new ApiResponse<>("Статус курса обновлен!", StudentCourseRequest.of(course)));
    }
//    @PatchMapping("/courses/{courseTitle}")
//    public ResponseEntity<?> checkedAnswer(@RequestParam String username,
//                                           @PathVariable String courseTitle) {
//
//        courseService.updateCourseStatus(courseTitle, username.);
//        StudentCourse course = courseService.getStudentCourse(courseTitle, username);
//        return ResponseEntity.ok(
//                new ApiResponse<>("Статус курса обновлен!", StudentCourseRequest.of(course)));
//    }
    @GetMapping("/rating/{type}/{course}")
    public ResponseEntity<?> getRating(
            @PathVariable String type,
            @PathVariable String course) {
        try {
            RatingStrategy<?> strategy = strategyFactory.getStrategy(type);
            RatingContext context = new RatingContext(strategy);
            return ResponseEntity.ok(
                    new ApiResponse<>("Рейтинг:",
                            context.executeStrategy(course)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

}
