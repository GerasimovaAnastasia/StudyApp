package edu.studyapp.repository;

import edu.studyapp.model.Literature;
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
public interface LiteratureRepository extends JpaRepository<Literature, Integer> {
    @Query(" SELECT l FROM Literature l WHERE l.title = :title")
    Optional<Literature> findByTitle(@Param("title")String title);
    @Query(" SELECT l FROM Literature l  WHERE l.course.title = :courseName")
    List<Literature> findByCourse(@Param("courseName")String courseName);
}
