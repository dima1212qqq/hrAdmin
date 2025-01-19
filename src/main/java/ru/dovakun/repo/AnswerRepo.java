package ru.dovakun.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.dovakun.data.entity.Answer;

import java.util.List;

public interface AnswerRepo extends JpaRepository<Answer, Long> {
    @Query("select a from Answer a where a.question.id = :questionId")
    List<Answer> findAllByQuestionId(Long questionId);
}
