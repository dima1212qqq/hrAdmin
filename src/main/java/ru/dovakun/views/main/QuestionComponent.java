package ru.dovakun.views.main;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import ru.dovakun.data.entity.Answer;
import ru.dovakun.data.entity.Question;
import ru.dovakun.services.AnswerService;
import ru.dovakun.services.QuestionService;

import java.util.List;

public class QuestionComponent extends VerticalLayout {
    private final TextField questionField = new TextField("Вопрос");
    private final VerticalLayout answersLayout = new VerticalLayout();
    private final HorizontalLayout buttonLayout;

    public QuestionComponent(Question question, List<Answer> answers, QuestionService questionService, AnswerService answerService,List<Question> questions) {
        questionField.setWidthFull();

        Button addAnswer = new Button("Добавить ответ");
        addAnswer.addClickListener(event -> addNewAnswer(answers, question, answerService));
        Button deleteQuestion = new Button("Удалить вопрос");
        deleteQuestion.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteQuestion.addClickListener(event -> {
            questionService.delete(question);
            answers.removeAll(question.getAnswers());
            questions.remove(question);
            if (this.getParent().isPresent() && this.getParent().get() instanceof VerticalLayout parentLayout) {
                parentLayout.remove(this);
            }
        });
        buttonLayout = new HorizontalLayout();
        buttonLayout.add(addAnswer, deleteQuestion);
        add(questionField, buttonLayout, answersLayout);
        questionField.addValueChangeListener(textFieldStringComponentValueChangeEvent -> {
            question.setQuestionText(textFieldStringComponentValueChangeEvent.getValue());
        });
    }



    private void addNewAnswer(List<Answer> answers, Question question, AnswerService answerService) {
        Answer answer = new Answer();
        answer.setQuestion(question);
        answers.add(answer);
        question.setAnswers(answers);
        AnswerComponent answerComponent = new AnswerComponent(answer, answerService, answers, question);
        answersLayout.add(answerComponent);
    }
    public void addQuestion(Question question, List<Answer> answers, AnswerService answerService) {
        questionField.setValue(question.getQuestionText()!=null?question.getQuestionText():"");
        question.setAnswers(answers);
        for (Answer answer : answers) {
            answer.setQuestion(question);
            AnswerComponent answerComponent = new AnswerComponent(answer, answerService,answers, question);
            answerComponent.addAnswer(answer);
            answersLayout.add(answerComponent);
        }
        add(questionField, buttonLayout, answersLayout);
    }

}
