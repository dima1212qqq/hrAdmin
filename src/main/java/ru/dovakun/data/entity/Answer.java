package ru.dovakun.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import ru.dovakun.data.AbstractEntity;

@Data
@Entity
public class Answer extends AbstractEntity {
    private String answerText;
    private double score;
    private boolean requires;
    private String detailsHint;
    @ManyToOne
    private Question question;
    @ManyToOne
    private Applicant applicant;
}
