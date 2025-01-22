package ru.dovakun.views.applicant;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ru.dovakun.data.entity.Applicant;
import ru.dovakun.data.entity.Question;
import ru.dovakun.data.entity.TestResult;
import ru.dovakun.data.entity.TestSession;
import ru.dovakun.repo.ApplicantRepo;
import ru.dovakun.repo.QuestionRepo;
import ru.dovakun.repo.TestResultRepo;
import ru.dovakun.views.testing.TestingView;

import java.util.ArrayList;
import java.util.List;

@Route("applicant")
@RolesAllowed("ADMIN")
public class ApplicantView extends VerticalLayout implements HasUrlParameter<Long> {
    private final ApplicantRepo applicantRepo;
    private final TestResultRepo testResultRepo;
    private final QuestionRepo questionRepo;

    public ApplicantView(ApplicantRepo applicantRepo, TestResultRepo testResultRepo, QuestionRepo questionRepo) {
        this.applicantRepo = applicantRepo;
        this.testResultRepo = testResultRepo;
        this.questionRepo = questionRepo;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Long aLong) {
        Applicant applicant = applicantRepo.findById(aLong).orElse(null);
        Grid<TestResult> grid = new Grid<>(TestResult.class, false);
        List<TestResult> testResults = testResultRepo.findAllByApplicantId(applicant.getId());
        grid.setItems(testResults);
        grid.addColumn(testResult->{
            Question question = questionRepo.findById(testResult.getQuestion().getId()).orElse(null);
            return question.getQuestionText();
        }).setHeader("Вопрос");
        grid.addColumn(TestResult::getSelectedAnswer).setHeader("Ответ");
        grid.addColumn(TestResult::getAdditionalDetails).setHeader("Подробный ответ");
        grid.addColumn(testResult -> {
            if (testResult.getAnsweredAt()<60){
                return "Меньше минуты";
            }else {
                return testResult.getAnsweredAt()/60;
            }
        }).setHeader("Время ответа");
        grid.addColumn(TestResult::getScore).setHeader("Баллы");
        add(grid);
    }
}
