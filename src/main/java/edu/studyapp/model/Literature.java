package edu.studyapp.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
@Entity
@Table(name = "literature")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Literature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String link;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}