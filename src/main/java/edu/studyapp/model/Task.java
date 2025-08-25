package edu.studyapp.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String taskContent;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
