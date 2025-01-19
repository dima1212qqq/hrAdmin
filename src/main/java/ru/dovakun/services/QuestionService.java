package ru.dovakun.services;

import org.springframework.stereotype.Service;
import ru.dovakun.data.entity.Question;
import ru.dovakun.repo.QuestionRepo;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepo questionRepo;

    public QuestionService(QuestionRepo questionRepo) {
        this.questionRepo = questionRepo;
    }
    public List<Question> findAllByTestId(Long testId) {
        return questionRepo.findAllByTestAssignmentId(testId);
    }
    public void save(Question question) {
        questionRepo.save(question);
    }
    public void saveAll(List<Question> question) {
        questionRepo.saveAll(question);
    }
}
