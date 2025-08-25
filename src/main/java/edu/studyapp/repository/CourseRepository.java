package edu.studyapp.repository;

import edu.studyapp.model.Course;
import edu.studyapp.model.CourseFile;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query(" SELECT c FROM Course c WHERE c.title = :title")
    Optional<Course> findByTitle(@Param("title") String title);

    @Query(" SELECT c FROM Course c  WHERE c.author.username = :username")
    List<Course> findByTeacher(@Param("username")String username);
}
