package ru.dovakun.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.dovakun.data.entity.Applicant;
import ru.dovakun.data.entity.Question;
import ru.dovakun.data.entity.TestResult;

import java.util.List;


public interface TestResultRepo extends JpaRepository<TestResult, Long> {
    @Query("select t from TestResult t where t.applicant.id in (:ids)")
    List<TestResult> findAllByApplicant(List<Long> ids);

    @Query("select t from TestResult t where t.applicant.id = :id")
    List<TestResult> findAllByApplicantId(Long id);

    @Query("select sum (t.score) from TestResult t where t.testSession.id = :id")
    Long findAllCalcScore(Long id);
    @Query("select t from TestResult t where t.applicant=:applicant and t.question = :question")
    TestResult findByApplicantAndQuestion(Applicant applicant, Question question);
}
