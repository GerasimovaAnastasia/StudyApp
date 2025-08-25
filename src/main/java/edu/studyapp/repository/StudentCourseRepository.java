package edu.studyapp.repository;

import edu.studyapp.model.Course;
import edu.studyapp.model.CourseFile;
import edu.studyapp.model.Student;
import edu.studyapp.model.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Integer> {
    @Query(" SELECT c FROM StudentCourse c  WHERE c.student.username = :username")
    List<StudentCourse> findByStudent(@Param("username") String username);

    @Query(" SELECT c FROM StudentCourse c  WHERE c.student.username = :username " +
            "AND c.course.title =:titleCourse")
    Optional<StudentCourse> findByStudentAndCourse(@Param("username") String username,
                                                   @Param("titleCourse") String titleCourse);

    @Query(" SELECT c FROM StudentCourse c  WHERE c.course.title = :courseName")
    List<StudentCourse> findByCourse(@Param("courseName") String courseName);

    @Query(" SELECT c FROM StudentCourse c WHERE c.course.title = :courseName " +
            "AND c.status = 'COMPLETED'")
    List<StudentCourse> findByStatus(String courseName);
}
