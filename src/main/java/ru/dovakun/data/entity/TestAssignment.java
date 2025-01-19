package ru.dovakun.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import ru.dovakun.data.AbstractEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class TestAssignment extends AbstractEntity {
    private String title;
    private String vacancyLink;
    private String description;
//    private String uniqueLink;
    @ManyToOne
    private User user;
//    @OneToMany(mappedBy = "testAssignment")
//    private List<Question> questions = new ArrayList<>();
//    @OneToMany(mappedBy = "testAssignment")
//    private List<Applicant> applicants = new ArrayList<>();
}
