package ru.dovakun.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import ru.dovakun.data.AbstractEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Question extends AbstractEntity {
    private String questionText;
    @ManyToOne
    private TestAssignment testAssignment;
    @OneToMany(mappedBy = "question",fetch = FetchType.EAGER)
    private List<Answer> answers = new ArrayList<>();

}
