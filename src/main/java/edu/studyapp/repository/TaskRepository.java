package edu.studyapp.repository;

import edu.studyapp.model.Course;
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
public interface TaskRepository extends JpaRepository<Task, Integer> {
    @Query(" SELECT t FROM Task t WHERE t.title = :title AND t.course.title =:courseName")
    Optional<Task> findByTitle(@Param("title")String title,
                               @Param("courseName")String courseName);
    @Query(" SELECT t FROM Task t WHERE t.course.title = :courseName")
    List<Task> findByCourse(@Param("courseName")String courseName);
}
