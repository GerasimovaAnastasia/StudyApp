package edu.studyapp.repository;

import edu.studyapp.model.CourseFile;
import edu.studyapp.model.Literature;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface CourseFileRepository extends JpaRepository<CourseFile, Integer> {
    @Query(" SELECT f FROM CourseFile f WHERE f.fileName = :fileName")
    Optional<CourseFile> findByFileName(@Param("fileName")String fileName);
    @Query(" SELECT f FROM CourseFile f  WHERE f.course.title = :courseName")
    List<CourseFile> findByCourse(@Param("courseName")String courseName);
}
