package edu.studyapp.service;

import edu.studyapp.dto.CourseFileRequest;
import edu.studyapp.model.CourseFile;
import edu.studyapp.repository.CourseFileRepository;
import edu.studyapp.service.exception.EntityNotFoundException;
import edu.studyapp.service.exception.ValidateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CourseFileService {
    private final CourseFileRepository courseFileRepository;
    private final CourseService courseService;
    private final ValidateHandler validateHandler;
    private static final Logger logger = LoggerFactory.getLogger(CourseFileService.class);

    public CourseFileService(CourseFileRepository courseFileRepository, CourseService courseService, ValidateHandler validateHandler) {
        this.courseFileRepository = courseFileRepository;
        this.courseService = courseService;
        this.validateHandler = validateHandler;
    }

    public void addCourseFile(String courseTitle, CourseFileRequest request) {
        Objects.requireNonNull(request, "CourseFileRequest не может быть null");
        logger.info("Добавление файла {} для курса: {}", request.fileName(), request.titleCourse());
        CourseFile courseFile = CourseFile.builder()
                .fileType(request.fileType())
                .filePath(request.filePath())
                .fileName(request.fileName())
                .course(courseService.getCourse(courseTitle))
                .build();
        courseFileRepository.save(courseFile);
    }
    public CourseFile getCourseFile(String title) {
        validateHandler.validateStringNotBlank(title);
        return courseFileRepository.findByFileName(title).orElseThrow(() -> {
            String errorMessage = String.format("Файл не найден: %s", title);
            logger.warn("{}: {}", "getCourseFile", errorMessage);
            return new EntityNotFoundException(errorMessage);
        });

    }
    public List<CourseFile> getAllCourseFiles() {

        return courseFileRepository.findAll();
    }
    public List<CourseFile> getAllCourseFilesByCourse(String courseName) {
        validateHandler.validateStringNotBlank(courseName);
        return courseFileRepository.findByCourse(courseName);
    }
    public void deleteCourseFile(String title) {
        logger.info("Удаление файла {}:", title);
        validateHandler.validateStringNotBlank(title);
        courseFileRepository.delete(getCourseFile(title));
    }

}
