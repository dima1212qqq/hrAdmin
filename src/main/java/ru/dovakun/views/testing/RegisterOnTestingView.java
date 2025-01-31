package ru.dovakun.views.testing;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.dao.DataIntegrityViolationException;
import ru.dovakun.views.GuestLayout;
import ru.dovakun.data.entity.Applicant;
import ru.dovakun.data.entity.Question;
import ru.dovakun.data.entity.TestAssignment;
import ru.dovakun.data.entity.TestSession;
import ru.dovakun.services.ApplicantService;
import ru.dovakun.services.QuestionService;
import ru.dovakun.services.TestAssignmentService;
import ru.dovakun.services.TestSessionService;
import ru.dovakun.util.HashCodeGenerator;

import java.time.OffsetDateTime;
import java.util.List;

@AnonymousAllowed
@Route(value = "/testing", layout = GuestLayout.class)
public class RegisterOnTestingView extends VerticalLayout implements HasUrlParameter<String> {
    private final TestAssignmentService testAssignmentService;
    private final QuestionService questionService;
    private final ApplicantService applicantService;
    private final TestSessionService testSessionService;

    private final TextField name = new TextField("Ваше имя");
    private final TextField linkHHRU = new TextField("Ссылка на ваше резюме");
    private final TextField feedback = new TextField("Способ связи, например телефон / email / telegram");
    private final Button startButton = new Button("Начать тестирование");

    public RegisterOnTestingView(TestAssignmentService testAssignmentService, QuestionService questionService,
                                 ApplicantService applicantService, TestSessionService testSessionService) {
        this.testAssignmentService = testAssignmentService;
        this.questionService = questionService;
        this.applicantService = applicantService;
        this.testSessionService = testSessionService;

        name.setRequired(true);
        linkHHRU.setRequired(true);
        feedback.setRequired(true);

        setAlignItems(Alignment.CENTER);

        startButton.setEnabled(false);
        name.addValueChangeListener(e -> toggleStartButton());
        linkHHRU.addValueChangeListener(e -> toggleStartButton());
        feedback.addValueChangeListener(e -> toggleStartButton());
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String s) {
        TestAssignment assignment = testAssignmentService.getByLink(s);
        if (assignment == null) {
            H1 error = new H1("Тестовое задание не найдено.");
            error.getStyle()
                    .set("color", "red")
                    .set("text-align", "center")
                    .set("font-size", "large");
            add(new Div(error));
        } else {
            HashCodeGenerator hashCodeGenerator = new HashCodeGenerator();

            H1 title = new H1(assignment.getTitle());
            title.getStyle()
                    .set("text-align", "center")
                    .set("font-size", "2em")
                    .set("margin-bottom", "10px");

            H1 description = new H1(assignment.getDescription());
            description.getStyle()
                    .set("text-align", "center")
                    .set("font-size", "1.2em")
                    .set("color", "#555")
                    .set("margin-bottom", "20px");

            Anchor link = new Anchor(assignment.getVacancyLink(), "Ссылка на вакансию");
            link.getStyle()
                    .set("text-decoration", "none")
                    .set("color", "#007BFF")
                    .set("font-size", "1em")
                    .set("margin-bottom", "20px");

            Text countQuestion = new Text(
                    String.format("В данном тесте %d вопросов", questionService.getQuestionsByTest(assignment.getId()).size())
            );
            Div countQuestionDiv = new Div(countQuestion);
            countQuestionDiv.getStyle()
                    .set("text-align", "center")
                    .set("font-size", "1em")
                    .set("margin-bottom", "20px");

            name.setPlaceholder("Введите ваше имя");
            name.getStyle().set("width", "300px");
            linkHHRU.setPlaceholder("Введите ссылку на ваше резюме");
            linkHHRU.getStyle().set("width", "300px");
            feedback.setPlaceholder("Введите способ связи, например телефон, email или Telegram");
            feedback.getStyle().set("width", "300px");

            startButton.addClassName("primary");
            startButton.getStyle()
                    .set("background-color", "#007BFF")
                    .set("color", "white")
                    .set("padding", "10px 20px")
                    .set("border-radius", "5px")
                    .set("font-size", "1em")
                    .set("cursor", "pointer")
                    .set("margin-top", "20px");

            setAlignItems(Alignment.CENTER);
            setJustifyContentMode(JustifyContentMode.CENTER);

            add(title, description, link, countQuestionDiv, name, linkHHRU, feedback, startButton);

            String ipAddress = VaadinRequest.getCurrent().getRemoteAddr();
            startButton.addClickListener(e -> {
                if (name.isEmpty() || linkHHRU.isEmpty() || feedback.isEmpty()) {
                    Notification notification = new Notification("Все поля должны быть заполнены!");
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setPosition(Notification.Position.MIDDLE);
                    notification.setDuration(3000);
                    notification.open();
                    return;
                }

                Applicant applicant = new Applicant();
                applicant.setName(name.getValue());
                applicant.setResumeLink(linkHHRU.getValue());
                applicant.setContactMethod(feedback.getValue());
                applicant.setTestAssignment(assignment);
                applicant.setIpAddress(ipAddress);
                applicant.setHashCode(hashCodeGenerator.generateUserHash(applicant.toString()));
                applicant.setStartTime(OffsetDateTime.now());

                try {
                    applicantService.save(applicant);

                    TestSession testSession = new TestSession();
                    testSession.setCurrentQuestionIndex(0);
                    testSession.setQuestions(questionService.getQuestionsByTest(assignment.getId()));
                    testSession.setHashCode(applicant.getHashCode());
                    testSession.setTestAssignment(assignment);
                    testSession.setApplicant(applicant);

                    testSessionService.save(testSession);
                    UI.getCurrent().navigate("test/" + testSession.getHashCode());
                } catch (DataIntegrityViolationException dive) {
                    Notification notification = new Notification("Пользователь уже зарегистрирован на этом тестировании!");
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setPosition(Notification.Position.MIDDLE);
                    notification.setDuration(3000);
                    notification.open();
                }
            });
        }
    }


    private void toggleStartButton() {
        startButton.setEnabled(!name.isEmpty() && !linkHHRU.isEmpty() && !feedback.isEmpty());
    }
}
