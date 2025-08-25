package edu.studyapp.model;


import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "teacher_profiles")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@SuperBuilder
public class Teacher extends User {
    private String position;

    public Teacher() {

    }

    public String getPersonalData() {
        return getFirstName() + " " + getLastName();
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "position='" + position + '\'' +
                '}';
    }
}
