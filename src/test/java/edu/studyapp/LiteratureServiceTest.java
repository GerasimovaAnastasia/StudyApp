package edu.studyapp;

import edu.studyapp.dto.LiteratureRequest;
import edu.studyapp.model.Course;
import edu.studyapp.model.Literature;
import edu.studyapp.repository.LiteratureRepository;
import edu.studyapp.service.CourseService;
import edu.studyapp.service.LiteratureService;
import edu.studyapp.service.exception.EntityNotFoundException;
import edu.studyapp.service.exception.ValidateHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LiteratureServiceTest {

    @Mock
    private LiteratureRepository literatureRepository;

    @Mock
    private CourseService courseService;

    @Mock
    private ValidateHandler validateHandler;

    @InjectMocks
    private LiteratureService literatureService;

    @Test
    void addLiterature_Success() {

        LiteratureRequest request = new LiteratureRequest("Book Title",
                "Author Name",
                "-",
                "title");
        Course course = new Course();
        course.setTitle("Math");

        when(courseService.getCourse("Math")).thenReturn(course);

        literatureService.addLiterature("Math", request);

        verify(literatureRepository, times(1)).save(any(Literature.class));
    }

    @Test
    void getLiterature_Success() {

        Literature literature = new Literature();
        literature.setTitle("Test Book");

        when(literatureRepository.findByTitle("Test Book")).thenReturn(Optional.of(literature));

        Literature result = literatureService.getLiterature("Test Book");

        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
    }

    @Test
    void getLiterature_NotFound_ThrowsException() {

        when(literatureRepository.findByTitle("Unknown Book")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            literatureService.getLiterature("Unknown Book");
        });
    }

    @Test
    void getAllLiteratures_Success() {

        Literature lit1 = new Literature();
        Literature lit2 = new Literature();
        when(literatureRepository.findAll()).thenReturn(List.of(lit1, lit2));

        List<Literature> result = literatureService.getAllLiteratures();

        assertEquals(2, result.size());
    }

    @Test
    void getAllLiteraturesByCourse_Success() {

        Literature lit1 = new Literature();
        Literature lit2 = new Literature();
        when(literatureRepository.findByCourse("Math")).thenReturn(List.of(lit1, lit2));

        List<Literature> result = literatureService.getAllLiteraturesByCourse("Math");

        assertEquals(2, result.size());
    }

    @Test
    void deleteLiterature_Success() {

        Literature literature = new Literature();
        literature.setTitle("To Delete");

        when(literatureRepository.findByTitle("To Delete")).thenReturn(Optional.of(literature));

        literatureService.deleteLiterature("To Delete");

        verify(literatureRepository, times(1)).delete(literature);
    }

    @Test
    void deleteLiterature_NotFound_ThrowsException() {

        when(literatureRepository.findByTitle("Unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            literatureService.deleteLiterature("Unknown");
        });
    }
}
