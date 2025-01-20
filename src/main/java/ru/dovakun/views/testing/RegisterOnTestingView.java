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
@Route(value = "/testing",layout = GuestLayout.class)
public class RegisterOnTestingView extends VerticalLayout
        implements HasUrlParameter<String> {
    private final TestAssignmentService testAssignmentService;
    private final QuestionService questionService;
    private final ApplicantService applicantService;
    private final TestSessionService testSessionService;
    private TextField name = new TextField("Ваше имя");
    private TextField linkHHRU = new TextField("Ссылка на ваше резюме");
    private TextField feedback  = new TextField("Способ связи, например телефон / email / telegram");
    private Button startButton = new Button("Начать тестирование");


    public RegisterOnTestingView(TestAssignmentService testAssignmentService, QuestionService questionService, ApplicantService applicantService, TestSessionService testSessionService) {
        this.testAssignmentService = testAssignmentService;
        name.setRequired(true);
        linkHHRU.setRequired(true);
        feedback.setRequired(true);
        setAlignItems(Alignment.CENTER);
        this.questionService = questionService;
        this.applicantService = applicantService;
        this.testSessionService = testSessionService;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String s) {
        TestAssignment assignment = testAssignmentService.getByLink(s);
        if (assignment == null) {
            H1 error = new H1("Тестовое задание не найдено.");
            error.getStyle().set("color", "red");
            error.getStyle().set("text-align", "center");
            error.getStyle().set("font-size", "large");
            add(new Div(error));
        }else {
            HashCodeGenerator hashCodeGenerator = new HashCodeGenerator();
            H1 title = new H1(assignment.getTitle());
            title.getStyle().set("color", "black");
            title.getStyle().set("text-align", "center");
            title.getStyle().set("font-size", "medium");
            title.getStyle().set("font-weight", "semi-bold");
            H1 description = new H1(assignment.getDescription());
            description.getStyle().set("color", "black");
            description.getStyle().set("text-align", "center");
            description.getStyle().set("font-size", "medium");
            description.getStyle().set("font-weight", "semi-bold");
            Anchor link = new Anchor(assignment.getVacancyLink(),assignment.getVacancyLink());
            List<Question> questions = questionService.getQuestionsByTest(assignment.getId());
            String countQuestionString = String.format("В данном тесте %d вопросов",questions.size());
            Text countQuestion = new Text(countQuestionString);
            add(title, description, link, countQuestion);
            add(name,linkHHRU,feedback,startButton);
            String ipAddress = VaadinRequest.getCurrent().getRemoteAddr();
            startButton.setEnabled(name.getValue() != null || linkHHRU.getValue() != null || feedback.getValue() != null);
            startButton.addClickListener(e -> {
                Applicant applicant = new Applicant();
                applicant.setName(name.getValue());
                applicant.setResumeLink(linkHHRU.getValue());
                applicant.setContactMethod(feedback.getValue());
                applicant.setTestAssignment(assignment);
                applicant.setIpAddress(ipAddress);
                applicant.setHashCode(hashCodeGenerator.generateUserHash(applicant.toString()));
                applicant.setStartTime(OffsetDateTime.now());
                applicantService.save(applicant);
                TestSession testSession = new TestSession();
                testSession.setCurrentQuestionIndex(0);
                testSession.setQuestions(questions);
                testSession.setHashCode(applicant.getHashCode());
                testSession.setTestAssignment(assignment);
                testSession.setApplicant(applicant);
                try {
                    testSessionService.save(testSession);
                    UI.getCurrent().navigate("test/"+testSession.getHashCode());
                }catch (DataIntegrityViolationException dive) {
                    Notification notification = new Notification("Пользователь уже зарегестрирован на этом тестирование!");
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setPosition(Notification.Position.BOTTOM_CENTER);
                    notification.open();
                    notification.setDuration(3000);
                }
            });
        }
    }
}
