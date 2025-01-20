package ru.dovakun.views.main;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import ru.dovakun.data.entity.Answer;
import ru.dovakun.data.entity.Question;
import ru.dovakun.services.AnswerService;

import java.util.List;

public class AnswerComponent extends FlexLayout {
    private final TextArea answerField = new TextArea("Ответ");
    private final NumberField scoreField = new NumberField("Баллы");
    private final Checkbox requiresDetails = new Checkbox("Нужен развёрнутый ответ");
    private final TextArea detailsHintField = new TextArea("Подсказка для развёрнутого ответа");

    public AnswerComponent(Answer answer, AnswerService answerService, List<Answer> answers, Question question) {
        setFlexDirection(FlexDirection.ROW);
        setFlexWrap(FlexWrap.WRAP);
        detailsHintField.setEnabled(false);
        answerField.addValueChangeListener(event -> {
            answer.setAnswerText(event.getValue());
        });
        scoreField.addValueChangeListener(event -> {
            answer.setScore(event.getValue());
        });
        detailsHintField.addValueChangeListener(event -> {
            answer.setDetailsHint(detailsHintField.getValue());
        });
        requiresDetails.addValueChangeListener(event -> {
            answer.setRequires(requiresDetails.getValue());
            if (!event.getValue()) {
                detailsHintField.setEnabled(false);
            }else {
                detailsHintField.setEnabled(true);
                detailsHintField.setValue(answer.getDetailsHint()!=null?answer.getDetailsHint(): "");
            }
        });
        Button deleteAnswer = new Button("Удалить ответ");
        deleteAnswer.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteAnswer.addClickListener(event -> {
            answerService.delete(answer);
            answers.remove(answer);
            question.getAnswers().remove(answer);
            this.removeAll();
        });
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        getStyle().set("gap", "10px");
        add(answerField, scoreField, requiresDetails, detailsHintField, deleteAnswer);
    }
    public void addAnswer(Answer answer) {
        answerField.setValue(answer.getAnswerText()!=null?answer.getAnswerText():"");
        scoreField.setValue(answer.getScore()!=0?answer.getScore():0D);
        requiresDetails.setValue(answer.isRequires());
        detailsHintField.setValue(answer.getDetailsHint()!=null?answer.getDetailsHint(): "");
    }
}
