package ru.dovakun.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import ru.dovakun.data.AbstractEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Applicant extends AbstractEntity {
    private String name;
    private String resumeLink;
    private String contactMethod;
    private String uniqueHash;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double score;
    private double completionPercentage;
    private String status; // New, Consider, Rejected, Accepted
    @ManyToOne
    private TestAssignment testAssignment;
    @OneToMany(mappedBy = "applicant")
    private List<Answer> answers = new ArrayList<>();
}
