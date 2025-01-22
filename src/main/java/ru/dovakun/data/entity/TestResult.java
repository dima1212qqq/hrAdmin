package ru.dovakun.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import ru.dovakun.data.AbstractEntity;

import java.time.OffsetDateTime;

@Entity
@Data
public class TestResult extends AbstractEntity {

    @ManyToOne
    private Applicant applicant;

    @ManyToOne
    private TestSession testSession;

    @ManyToOne
    private Question question;

    private String selectedAnswer;

    private double score;

    private String additionalDetails;

    private Long answeredAt;

    private OffsetDateTime answeredAtTime;
}
