package ru.dovakun.views.applicant;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ru.dovakun.data.entity.Applicant;
import ru.dovakun.data.entity.Question;
import ru.dovakun.data.entity.TestResult;
import ru.dovakun.repo.ApplicantRepo;
import ru.dovakun.repo.QuestionRepo;
import ru.dovakun.repo.TestResultRepo;

import java.util.List;
import java.util.stream.Stream;

@Route("applicant")
@PageTitle("Ответы соискателя")
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
        Span nameSpan = new Span(applicant.getName());
        nameSpan.getStyle()
                .set("font-weight", "bold")
                .set("font-size", "16px")
                .set("margin-bottom", "5px");

        Span contactMethod = new Span("Контакт: " + applicant.getContactMethod());
        contactMethod.getStyle()
                .set("color", "gray")
                .set("font-size", "14px")
                .set("margin-bottom", "5px");

        Anchor resumeLink = new Anchor(applicant.getResumeLink(), "Резюме : " + applicant.getResumeLink());
        resumeLink.setTarget("_blank");
        resumeLink.getStyle()
                .set("font-size", "14px")
                .set("color", "blue")
                .set("text-decoration", "underline");

        VerticalLayout layout = new VerticalLayout(nameSpan, contactMethod, resumeLink);
        layout.setAlignItems(Alignment.START);
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.getStyle()
                .set("border", "1px solid #ddd")
                .set("border-radius", "8px")
                .set("padding", "10px")
                .set("background-color", "#f9f9f9");


        add(layout);
        Button backButton = new Button("Назад", event -> {
            UI.getCurrent().navigate("applicants/"+applicant.getTestAssignment().getId());
        });
        Grid<TestResult> grid = new Grid<>(TestResult.class, false);
        List<TestResult> testResults = testResultRepo.findAllByApplicantId(applicant.getId());
        grid.setItems(testResults);
        grid.addColumn(testResult->{
            Question question = questionRepo.findById(testResult.getQuestion().getId()).orElse(null);
            return question.getQuestionText();
        }).setHeader("Вопрос").setResizable(true);
        grid.addColumn(TestResult::getSelectedAnswer).setHeader("Ответ").setResizable(true);
        grid.addColumn(testResult -> {
            if (testResult.getAdditionalDetails()==null){
                return "Нет подробного ответа";
            }else {
                return "Подробный ответ есть";
            }
        }).setHeader("Подробный ответ").setResizable(true).setClassName("wrap-text");;
        grid.addColumn(testResult -> {
            if (testResult.getAnsweredAt()<60){
                return "Меньше минуты";
            }else {
                return testResult.getAnsweredAt()/60;
            }
        }).setHeader("Время ответа").setResizable(true);
        grid.addColumn(TestResult::getScore).setHeader("Баллы").setResizable(true);
        add(backButton,grid);
        grid.setItemDetailsRenderer(createApplicantDetailsRenderer());
    }

    private static ComponentRenderer<ApplicantDetailsFormLayout, TestResult> createApplicantDetailsRenderer() {
        return new ComponentRenderer<>(() -> {
            ApplicantDetailsFormLayout detailsForm = new ApplicantDetailsFormLayout();
            return detailsForm;
        }, (detailsForm, testResult) -> {
            if (testResult.getAdditionalDetails()!=null){
                detailsForm.setVisible(true);
                detailsForm.setPerson(testResult);
            }
        });
    }

    private static class ApplicantDetailsFormLayout extends FormLayout {
        private final TextArea detailsArea = new TextArea("Подробный ответ");


        public ApplicantDetailsFormLayout() {
            Stream.of(detailsArea).forEach(field -> {
                field.setReadOnly(true);
                add(field);
            });

            setResponsiveSteps(new ResponsiveStep("0", 3));
            setColspan(detailsArea, 3);
        }

        public void setPerson(TestResult testResult) {
            detailsArea.setWidthFull();
            if (testResult.getAdditionalDetails()!=null){
                detailsArea.setValue(testResult.getAdditionalDetails());
            }
        }
    }
}
