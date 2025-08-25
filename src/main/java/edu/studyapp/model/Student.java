package edu.studyapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "student_profiles")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@SuperBuilder
public class Student extends User {
    private String groupName;

    public Student() {
    }
//    public static StudentBuilder builder() {
//        return new StudentBuilder();
//    }
//
//    public static class StudentBuilder extends UserBuilder {
//        private UserBuilder userBuilder = User.builder();
//        private String groupName;
//
//        public StudentBuilder groupName(String groupName) {
//            this.groupName = groupName;
//            return this;
//        }
//
//        @Override
//        public Student build() {
//            Student student = new Student();
//            student.setUsername(super.username);
//            student.setPassword(super.password);
//            student.setRole(super.role);
//            student.setFirstName(super.firstName);
//            student.setLastName(super.lastName);
//            student.setGroupName(groupName);
//            return student;
//        }
//    }
//    public String getGroupName() {
//        return groupName;
//    }
//    public void setGroupName(String groupName) {
//        this.groupName = groupName;
//    }

    @Override
    public String toString() {
        return "Student{" +
                "groupName='" + groupName + '\'' +
                '}';
    }
}