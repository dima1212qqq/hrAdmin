package ru.dovakun.services;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dovakun.data.entity.Answer;
import ru.dovakun.data.entity.Question;
import ru.dovakun.repo.AnswerRepo;
import ru.dovakun.repo.QuestionRepo;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepo questionRepo;
    private final AnswerRepo answerRepo;

    public QuestionService(QuestionRepo questionRepo, AnswerRepo answerRepo) {
        this.questionRepo = questionRepo;
        this.answerRepo = answerRepo;
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
    @Transactional
    @Modifying
    public void delete(Question question) {
        List<Answer> answers = answerRepo.findAllByQuestionId(question.getId());
        if (!answers.isEmpty()) {
            answerRepo.deleteAll(answers);
        }
        questionRepo.delete(question);
    }

    public List<Question> getQuestionsByTest(Long testId) {
        return questionRepo.findAllByTestAssignmentId(testId);
    }
}
