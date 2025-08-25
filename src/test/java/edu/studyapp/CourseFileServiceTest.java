package edu.studyapp;

import edu.studyapp.dto.CourseFileRequest;
import edu.studyapp.model.Course;
import edu.studyapp.model.CourseFile;
import edu.studyapp.repository.CourseFileRepository;
import edu.studyapp.service.CourseFileService;
import edu.studyapp.service.CourseService;
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
class CourseFileServiceTest {

    @Mock
    private CourseFileRepository courseFileRepository;

    @Mock
    private CourseService courseService;
    @Mock
    private ValidateHandler validateHandler;

    @InjectMocks
    private CourseFileService courseFileService;

    @Test
    void addCourseFile_Success() {
        CourseFileRequest request = new CourseFileRequest(
                "lecture.pdf", "PDF", "files/lecture.pdf", "Math Course"
        );

        Course course = new Course();
        when(courseService.getCourse("Math")).thenReturn(course);

        courseFileService.addCourseFile("Math", request);

        verify(courseFileRepository, times(1)).save(any(CourseFile.class));
    }

    @Test
    void getCourseFile_Success() {

        CourseFile courseFile = new CourseFile();
        courseFile.setFileName("test.pdf");

        when(courseFileRepository.findByFileName("test.pdf")).thenReturn(Optional.of(courseFile));

        CourseFile result = courseFileService.getCourseFile("test.pdf");

        assertNotNull(result);
        assertEquals("test.pdf", result.getFileName());
    }
    @Test
    void getCourseFile_NotFound_ThrowsException() {

        when(courseFileRepository.findByFileName("unknown.pdf")).thenReturn(Optional.empty());


        assertThrows(EntityNotFoundException.class, () -> {
            courseFileService.getCourseFile("unknown.pdf");
        });
    }
    @Test
    void getAllCourseFilesByCourse_Success() {

        CourseFile file1 = new CourseFile();
        CourseFile file2 = new CourseFile();
        when(courseFileRepository.findByCourse("Math")).thenReturn(List.of(file1, file2));

        List<CourseFile> result = courseFileService.getAllCourseFilesByCourse("Math");

        assertEquals(2, result.size());
    }
    @Test
    void deleteCourseFile_Success() {

        CourseFile courseFile = new CourseFile();
        courseFile.setFileName("toDelete.pdf");

        when(courseFileRepository.findByFileName("toDelete.pdf")).thenReturn(Optional.of(courseFile));

        courseFileService.deleteCourseFile("toDelete.pdf");

        verify(courseFileRepository, times(1)).delete(courseFile);
    }
    @Test
    void deleteCourseFile_NotFound() {

        when(courseFileRepository.findByFileName("unknown.pdf")).thenReturn(Optional.empty());


        assertThrows(EntityNotFoundException.class, () -> {
            courseFileService.deleteCourseFile("unknown.pdf");
        });
    }

}
