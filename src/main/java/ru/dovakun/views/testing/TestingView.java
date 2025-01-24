package ru.dovakun.views.testing;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.hibernate.Hibernate;
import ru.dovakun.repo.TestResultRepo;
import ru.dovakun.views.GuestLayout;
import ru.dovakun.data.dto.AnswerDTO;
import ru.dovakun.data.entity.*;
import ru.dovakun.repo.QuestionRepo;
import ru.dovakun.services.*;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@AnonymousAllowed
@Route(value = "/test", layout = GuestLayout.class)
public class TestingView extends VerticalLayout
        implements HasUrlParameter<String> {

    private final TestSessionService testSessionService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final QuestionRepo questionRepo;
    private final ApplicantService applicantService;
    private final TestResultService testResultService;
    private final TestResultRepo testResultRepo;

    public TestingView(ApplicantService applicantService, QuestionService questionService, TestSessionService testSessionService, AnswerService answerService, QuestionRepo questionRepo, TestResultService testResultService, TestResultRepo testResultRepo) {
        this.testSessionService = testSessionService;
        this.questionService = questionService;
        this.answerService = answerService;
        this.questionRepo = questionRepo;
        this.applicantService = applicantService;
        this.testResultService = testResultService;
        this.testResultRepo = testResultRepo;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String s) {
        setJustifyContentMode(JustifyContentMode.AROUND);
        setAlignItems(Alignment.CENTER);
        TestSession session = testSessionService.getByHash(s);
        Hibernate.initialize(session);
        List<Question> questions;
        if (session != null) {
            questions = questionService.getQuestionsByTest(session.getTestAssignment().getId());
        } else {
            questions = new ArrayList<>();
        }
        if (session != null && session.getCurrentQuestionIndex() < questions.size()) {
            Applicant applicant = session.getApplicant();
            TestResult result = new TestResult();
            TextArea textArea = new TextArea();
            Question question = questions.get(session.getCurrentQuestionIndex());
            Div title = new Div(question.getQuestionText());
            String counter = String.format("%d/%d вопросов", session.getCurrentQuestionIndex() + 1, questions.size());
            Div count = new Div(counter);
            VerticalLayout layout = new VerticalLayout();
            layout.setAlignItems(Alignment.CENTER);
            List<AnswerDTO> answers = getAnswersByQuestionId(question.getId());
            RadioButtonGroup<AnswerDTO> answerRadioButtonGroup = new RadioButtonGroup<>();
            layout.add(answerRadioButtonGroup);
            answerRadioButtonGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

            Button continueButton = new Button("Продолжить");
            answerRadioButtonGroup.setItems(answers);
            answerRadioButtonGroup.setItemLabelGenerator(AnswerDTO::getTitle);
            textArea.setVisible(false);
            textArea.setWidth(answerRadioButtonGroup.getWidth());
            answerRadioButtonGroup.addValueChangeListener(event -> {
                continueButton.setEnabled(true);
                result.setSelectedAnswer(event.getValue().getTitle());
                result.setScore(event.getValue().getScore());
                if (event.getValue().isRequires()) {
                    textArea.setVisible(true);
                    textArea.setLabel(event.getValue().getDetailsHint());
                    add(textArea);
                } else {
                    textArea.setVisible(false);
                }
            });
            continueButton.setEnabled(false);
            continueButton.addClickListener(event -> {
                if (session.getCurrentQuestionIndex() == 0) {
                    Duration duration = Duration.between(applicant.getStartTime(), OffsetDateTime.now());
                    long seconds = duration.toSeconds();
                    result.setAnsweredAt(seconds);
                    result.setAnsweredAtTime(OffsetDateTime.now());
                }else if(session.getCurrentQuestionIndex() == questions.size()){
                    Question previousQuestion = questions.get(session.getCurrentQuestionIndex() - 1);
                    TestResult prevTestResult = testResultRepo.findByApplicantAndQuestion(applicant, previousQuestion);
                    Duration duration = Duration.between(prevTestResult.getAnsweredAtTime(), OffsetDateTime.now());
                    long seconds = duration.toSeconds();
                    result.setAnsweredAt(seconds);
                    result.setAnsweredAtTime(OffsetDateTime.now());

                }else {
                    Question previousQuestion = questions.get(session.getCurrentQuestionIndex() - 1);
                    TestResult prevTestResult = testResultRepo.findByApplicantAndQuestion(applicant, previousQuestion);
                    Duration duration = Duration.between(prevTestResult.getAnsweredAtTime(), OffsetDateTime.now());
                    long seconds = duration.toSeconds();
                    result.setAnsweredAt(seconds);
                    result.setAnsweredAtTime(OffsetDateTime.now());
                }
                session.setCurrentQuestionIndex(session.getCurrentQuestionIndex() + 1);
                testSessionService.save(session);
                result.setApplicant(session.getApplicant());
                result.setTestSession(session);
                result.setQuestion(question);
                if (textArea.isVisible()) {
                    result.setAdditionalDetails(textArea.getValue());
                }
                testResultService.save(result);
                refreshView(beforeEvent, s);
            });
            add(title, count, layout, textArea, continueButton);
        } else if (session != null && session.getCurrentQuestionIndex() == questions.size()) {
            Applicant applicant = session.getApplicant();
            if (applicant.getEndTime()==null){
                applicant.setEndTime(OffsetDateTime.now());
                applicantService.save(applicant);
            }
            session.setCompleted(true);
            testSessionService.save(session);
            add(new H1("Спасибо, вы завершили тестирование!"));
        } else {
            add(new H1("Теста не существует!"));
        }
    }

    private void refreshView(BeforeEvent beforeEvent, String s) {
        this.removeAll();
        setParameter(beforeEvent, s);
    }

    public List<AnswerDTO> getAnswersByQuestionId(Long questionId) {
        List<Answer> answers = answerService.getAnswersByQuestionId(questionId);
        List<AnswerDTO> answerDTOs = new ArrayList<>();
        for (Answer answer : answers) {
            AnswerDTO answerDTO = new AnswerDTO();
            answerDTO.setTitle(answer.getAnswerText());
            answerDTO.setScore(answer.getScore());
            answerDTO.setRequires(answer.isRequires());
            answerDTO.setDetailsHint(answer.getDetailsHint());
            answerDTOs.add(answerDTO);
        }
        return answerDTOs;
    }
}
