package edu.studyapp.repository;

import edu.studyapp.model.StudentAnswer;
import edu.studyapp.model.Task;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Integer> {
    @Query("SELECT a FROM StudentAnswer a  " +
            "WHERE a.task.course.title = :courseName " +
            "AND a.grade IS NULL")
    List<StudentAnswer> uncheckedAnswer(@Param("courseName") String courseName);

    @Query("SELECT a FROM StudentAnswer a  " +
            "WHERE a.task.title = :title " +
            "AND a.student.username =:username " +
            "AND a.task.course.title =:courseTitle")
    Optional<StudentAnswer> getTaskByTitle(@Param("title") String title,
                                          @Param("username") String username,
                                          @Param("courseTitle") String courseTitle);

    @Query("SELECT a FROM StudentAnswer a  " +
            "WHERE a.student.username = :username " +
            "AND a.task.course.title = :courseTitle")
    List<StudentAnswer> getStudentAnswerByStudent (@Param ("username") String username,
                                                   @Param("courseTitle") String courseTitle);
}