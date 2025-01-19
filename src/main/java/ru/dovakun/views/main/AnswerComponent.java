package ru.dovakun.views.main;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import ru.dovakun.data.entity.Answer;

public class AnswerComponent extends FlexLayout {
    private final TextArea answerField = new TextArea("Ответ");
    private final TextField scoreField = new TextField("Баллы");
    private final Checkbox requiresDetails = new Checkbox("Нужен развёрнутый ответ");
    private final TextArea detailsHintField = new TextArea("Подсказка для развёрнутого ответа");

    public AnswerComponent(Answer answer) {
        setFlexDirection(FlexDirection.ROW);
        setFlexWrap(FlexWrap.WRAP);
        detailsHintField.setEnabled(false);
        answerField.addValueChangeListener(event -> {
            answer.setAnswerText(event.getValue());
        });
        scoreField.addValueChangeListener(event -> {
            answer.setScore(Double.parseDouble(event.getValue()));
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
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        getStyle().set("gap", "10px");
        add(answerField, scoreField, requiresDetails, detailsHintField);
    }
    public void addAnswer(Answer answer) {
        answerField.setValue(answer.getAnswerText());
        scoreField.setValue(String.valueOf(answer.getScore()));
        requiresDetails.setValue(answer.isRequires());
        detailsHintField.setValue(answer.getDetailsHint()!=null?answer.getDetailsHint(): "");
    }
}
