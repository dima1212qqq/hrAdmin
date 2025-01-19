package ru.dovakun.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.dovakun.data.entity.Question;

import java.util.List;

public interface QuestionRepo extends JpaRepository<Question, Long> {
    @Query("select q from Question q where q.testAssignment.id = :testId")
    List<Question> findAllByTestAssignmentId(Long testId);
}
