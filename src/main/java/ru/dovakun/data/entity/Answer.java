package ru.dovakun.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Question question;
    @ManyToOne
    private Applicant applicant;
}
