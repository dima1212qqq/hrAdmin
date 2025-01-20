package ru.dovakun.data.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.dovakun.Application;
import ru.dovakun.data.AbstractEntity;

import java.util.List;

@Entity
@Data
@Table(name = "test_session", uniqueConstraints = {@UniqueConstraint(columnNames = {"test_assignment_id","hash_code"})})
public class TestSession extends AbstractEntity {

    private String hashCode;
    private int currentQuestionIndex;
    private boolean isCompleted;
    @ManyToMany
    private List<Question> questions;
    @ManyToOne()
    private TestAssignment testAssignment;
    @OneToOne
    private Applicant applicant;

}
