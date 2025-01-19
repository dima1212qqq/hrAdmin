package ru.dovakun.views.main;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import ru.dovakun.data.entity.Answer;
import ru.dovakun.data.entity.Question;

import java.util.List;

public class QuestionComponent extends VerticalLayout {
    private final TextField questionField = new TextField("Вопрос");
    private final Button addAnswer = new Button("Добавить ответ");
    private final VerticalLayout answersLayout = new VerticalLayout();

    public QuestionComponent(Question question,List<Answer> answers) {
        questionField.setWidthFull();

        addAnswer.addClickListener(event -> addNewAnswer(answers, question));

        add(questionField, addAnswer, answersLayout);
        questionField.addValueChangeListener(textFieldStringComponentValueChangeEvent -> {
            question.setQuestionText(textFieldStringComponentValueChangeEvent.getValue());
        });
    }

    private void addNewAnswer(List<Answer> answers, Question question) {
        Answer answer = new Answer();
        answer.setQuestion(question);
        answers.add(answer);
        question.setAnswers(answers);
        AnswerComponent answerComponent = new AnswerComponent(answer);
        answersLayout.add(answerComponent);
    }
    public void addQuestion(Question question, List<Answer> answers) {
        questionField.setValue(question.getQuestionText()!=null?question.getQuestionText():"");
        question.setAnswers(answers);
        for (Answer answer : answers) {
            answer.setQuestion(question);
            AnswerComponent answerComponent = new AnswerComponent(answer);
            answerComponent.addAnswer(answer);
            answersLayout.add(answerComponent);
        }
        add(questionField, addAnswer,answersLayout);
    }

}
