package edu.studyapp.service;

import edu.studyapp.dto.LiteratureRequest;
import edu.studyapp.model.Literature;
import edu.studyapp.repository.LiteratureRepository;
import edu.studyapp.service.exception.EntityNotFoundException;
import edu.studyapp.service.exception.ValidateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class LiteratureService {
    private final LiteratureRepository literatureRepository;
    private final CourseService courseService;
    private final ValidateHandler validateHandler;
    private static final Logger logger = LoggerFactory.getLogger(LiteratureService.class);

    public LiteratureService(LiteratureRepository literatureRepository, CourseService courseService, ValidateHandler validateHandler) {
        this.literatureRepository = literatureRepository;
        this.courseService = courseService;
        this.validateHandler = validateHandler;
    }

    public void addLiterature(String courseTitle, LiteratureRequest request) {
        Objects.requireNonNull(request, "LiteratureRequest не может быть null");
        logger.info("Добавление литературы {} для курса: {}", request.title(), courseTitle);

        Literature literature = Literature.builder()
                .author(request.authorBook())
                .title(request.title())
                .course(courseService.getCourse(courseTitle))
                .build();
        literatureRepository.save(literature);
    }
    public Literature getLiterature(String title) {
        validateHandler.validateStringNotBlank(title);
        return literatureRepository.findByTitle(title).orElseThrow(() ->
        {
            String errorMessage = String.format("Литература не найдена: %s", title);
            logger.warn("{}: {}", "getLiterature", errorMessage);
            return new EntityNotFoundException(errorMessage);
        });
    }
    public List<Literature> getAllLiteratures() {
        return literatureRepository.findAll();
    }
    public List<Literature> getAllLiteraturesByCourse(String courseName) {
        validateHandler.validateStringNotBlank(courseName);
        return literatureRepository.findByCourse(courseName);
    }
    public void deleteLiterature(String title) {
        logger.info("Удаление литературы {}:", title);
        validateHandler.validateStringNotBlank(title);
        literatureRepository.delete(getLiterature(title));
    }
}
