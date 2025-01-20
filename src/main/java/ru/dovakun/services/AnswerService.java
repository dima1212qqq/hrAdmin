package ru.dovakun.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dovakun.data.entity.Answer;
import ru.dovakun.repo.AnswerRepo;

import java.util.List;

@Service
public class AnswerService {
    private final AnswerRepo answerRepo;

    public AnswerService(AnswerRepo answerRepo) {
        this.answerRepo = answerRepo;
    }

    public List<Answer> getAnswersByQuestionId(Long questionId) {
        return answerRepo.findAllByQuestionId(questionId);
    }

    public void save(List<Answer> answers) {
        answerRepo.saveAll(answers);
    }
    @Transactional
    public void delete(Answer answer) {
        answerRepo.delete(answer);
    }
}
