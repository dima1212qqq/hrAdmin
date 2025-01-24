package ru.dovakun.views.main;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import ru.dovakun.data.entity.Answer;
import ru.dovakun.data.entity.Question;
import ru.dovakun.data.entity.TestAssignment;
import ru.dovakun.security.AuthenticatedUser;
import ru.dovakun.services.AnswerService;
import ru.dovakun.services.QuestionService;
import ru.dovakun.services.TestAssignmentService;

import java.util.List;

public class TestAssignmentForm extends VerticalLayout {
    private final MainView mainView;
    private List<Question> questions;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private TestAssignment testAssignment;

    private final TextField titleField = new TextField("Название");
    private final TextField linkField = new TextField("Ссылка на вакансию");
    private final TextArea descriptionField = new TextArea("Описание");
    private final TextField uniqueLink = new TextField("Уникальная ссылка на тест");
    private final VerticalLayout questionsLayout = new VerticalLayout();
    private final Accordion accordion;

    public TestAssignmentForm(MainView mainView, QuestionService questionService,
                              AnswerService answerService, TestAssignmentService testAssignmentService,
                              AuthenticatedUser authenticatedUser) {
        this.answerService = answerService;
        this.mainView = mainView;
        this.questionService = questionService;
        accordion = new Accordion();
        accordion.setWidthFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        FlexLayout  hLayout = new FlexLayout ();
        hLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        hLayout.setJustifyContentMode(FlexLayout.JustifyContentMode.START);
        hLayout.getStyle().set("padding", "5px");
        hLayout.getStyle().set("gap","10px");
        hLayout.setAlignItems(Alignment.CENTER);
        Anchor link = new Anchor("testing/", "testing/");
        uniqueLink.setPrefixComponent(link);
        descriptionField.setMaxLength(255);
        descriptionField.setValueChangeMode(ValueChangeMode.EAGER);
        descriptionField.setSizeFull();
        descriptionField.addValueChangeListener(e -> {
            e.getSource()
                    .setHelperText(e.getValue().length() + "/" + "255");
        });
        questionsLayout.setVisible(false);
        Button addQuestion = new Button("Добавить вопрос");
        addQuestion.addClickListener(event -> {
            if (!questionsLayout.isVisible()){
                questionsLayout.setVisible(true);
            }
            addNewQuestion();
        });
        hLayout.add(titleField, linkField, uniqueLink);
        FlexLayout buttonsLayout = new FlexLayout();
        Button cansel = new Button("Вернутся назад");
        cansel.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cansel.addClickListener(event -> {
            mainView.showForm(false);
            questionsLayout.removeAll();
        });
        Button saveTest = new Button("Сохранить тестовое задание");
        saveTest.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        saveTest.addClickListener(event -> {
            testAssignment.setTitle(titleField.getValue()!=null?titleField.getValue():"");
            testAssignment.setDescription(descriptionField.getValue()!=null?descriptionField.getValue():"");
            testAssignment.setVacancyLink(linkField.getValue()!=null?linkField.getValue():"");
            testAssignment.setUniqueLink(uniqueLink.getValue()!=null?uniqueLink.getValue():"");
            testAssignmentService.save(testAssignment);
            questionService.saveAll(questions);
            for (Question question : questions) {
                answerService.save(question.getAnswers());
            }
            questionsLayout.removeAll();
            mainView.refreshGrid(authenticatedUser.get().get());
            mainView.showForm(false);
        });
        buttonsLayout.add(addQuestion, saveTest, cansel);
        buttonsLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        buttonsLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonsLayout.setAlignItems(Alignment.CENTER);
        buttonsLayout.setFlexDirection(FlexLayout.FlexDirection.ROW);
        buttonsLayout.getStyle().set("gap", "10px");
//        add(hLayout,descriptionField,questionsLayout,buttonsLayout);
        add(hLayout,descriptionField,accordion,buttonsLayout);
    }

    private void addNewQuestion() {
        Question question = new Question();
        questions.add(question);
        question.setTestAssignment(mainView.currentTestAssignment);
        List<Answer> answers = question.getAnswers();
        QuestionComponent questionComponent = new QuestionComponent(question,answers, questionService, answerService,questions,accordion);
        questionsLayout.add(questionComponent);
    }

    public void setTestAssignment(TestAssignment testAssignment) {
        if (testAssignment != null) {
            accordion.getChildren().forEach(Component::removeFromParent);
            this.testAssignment = testAssignment;
            titleField.setValue(testAssignment.getTitle() != null ? testAssignment.getTitle() : "");
            linkField.setValue(testAssignment.getVacancyLink() != null ? testAssignment.getVacancyLink() : "");
            descriptionField.setValue(testAssignment.getDescription() != null ? testAssignment.getDescription() : "");
            questions = questionService.findAllByTestId(testAssignment.getId());
            uniqueLink.setValue(testAssignment.getUniqueLink() != null ? testAssignment.getUniqueLink() : "");
            for (Question question : questions) {
                List<Answer> answers = answerService.getAnswersByQuestionId(question.getId());
                QuestionComponent questionComponent = new QuestionComponent(question, answers,questionService, answerService,questions, accordion);
                questionComponent.addQuestion(question, answers, answerService);
                questionsLayout.add(questionComponent);
                questionsLayout.setVisible(true);
            }
        } else {
            titleField.clear();
            linkField.clear();
            descriptionField.clear();
            questions.clear();
        }
    }
}

