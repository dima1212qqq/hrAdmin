package ru.dovakun.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import ru.dovakun.data.AbstractEntity;
@Data
@Entity
public class AnswerOption extends AbstractEntity {
    private String text;
    private int score;
    private boolean requiresExpandedAnswer;
    private String hintText;
    @ManyToOne
    private Question question;
}
