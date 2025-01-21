package ru.dovakun.views.applicant;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;
import ru.dovakun.data.entity.Applicant;
import ru.dovakun.data.entity.TestAssignment;
import ru.dovakun.data.entity.TestResult;
import ru.dovakun.data.entity.TestSession;
import ru.dovakun.data.enums.Status;
import ru.dovakun.security.AuthenticatedUser;
import ru.dovakun.services.ApplicantService;
import ru.dovakun.services.TestAssignmentService;
import ru.dovakun.services.TestResultService;
import ru.dovakun.services.TestSessionService;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Route("/applicant")
@RolesAllowed("ADMIN")
@Menu(order = 1, icon = LineAwesomeIconUrl.PEOPLE_CARRY_SOLID)
public class ApplicantView extends VerticalLayout {

    private final TestResultService testResultService;
    private final AuthenticatedUser authenticatedUser;
    private final ApplicantService applicantService;
    private final TestAssignmentService testAssignmentService;
    private final TestSessionService testSessionService;

    public ApplicantView(TestResultService testResultService, AuthenticatedUser authenticatedUser, ApplicantService applicantService, TestAssignmentService testAssignmentService, TestSessionService testSessionService) {
        Grid<Applicant> grid = new Grid<>(Applicant.class, false);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        this.testResultService = testResultService;
        this.applicantService = applicantService;
        this.testAssignmentService = testAssignmentService;
        List<TestAssignment> testAssignments = testAssignmentService.getTestAssignmentsByUser(authenticatedUser.get().get().getId());
        List<Applicant> applicants = applicantService.findAllByTest(testAssignments);
//        List<TestResult> testResults = testResultService.findAllByApplicants(applicants);
        this.testSessionService = testSessionService;
        List<TestSession> testSessions = testSessionService.findAllByApplicants(applicants);
        Map<Long, TestSession> sessionMap = testSessions.stream()
                .collect(Collectors.toMap(ts -> ts.getApplicant().getId(), ts -> ts));

        grid.addColumn(createApplicantRenderer())
                .setHeader("Имя и Ссылка")
                .setAutoWidth(true)
                .setFlexGrow(1);
        grid.addColumn(createProgressAndScoreRenderer(sessionMap,testResultService))
                .setHeader("Прогресс и Баллы")
                .setAutoWidth(true)
                .setFlexGrow(1);

        grid.addColumn(createTimeRenderer(formatter))
                .setHeader("Время")
                .setAutoWidth(true)
                .setFlexGrow(1);

        grid.addColumn(applicant -> {
            TestSession session = sessionMap.get(applicant.getId());

            if (session != null && session.isCompleted()) {
                OffsetDateTime startTime = applicant.getStartTime();
                OffsetDateTime endTime = applicant.getEndTime();

                if (startTime != null && endTime != null) {
                    Duration duration = Duration.between(startTime, endTime);
                    long minutes = duration.toMinutes();
                    return minutes + " минут" + (minutes == 1 ? "а" : "");
                } else {
                    return "Данные отсутствуют";
                }
            } else {
                return "Ещё не закончил";
            }
        }).setHeader("Затраченное время");
        grid.addComponentColumn(applicant -> {
            ComboBox<Status> statusComboBox = new ComboBox<>();
            statusComboBox.setItems(Status.values());
            statusComboBox.setItemLabelGenerator(Status::getTranslationKey);
            statusComboBox.setValue(applicant.getStatus());
            statusComboBox.addValueChangeListener(event -> {
                if (event.getValue() != null) {
                    applicant.setStatus(event.getValue());
                    applicantService.save(applicant);
                    applicant.setStatus(event.getValue());
                    applicantService.save(applicant);
                    grid.getDataProvider().refreshItem(applicant);
                }
            });
            return statusComboBox;
        }).setHeader("Статус");
        grid.setItems(applicants);
        this.authenticatedUser = authenticatedUser;
        add(grid);
    }

    private static Renderer<Applicant> createApplicantRenderer() {
        return LitRenderer.<Applicant>of(
                        "<vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">" +
                                "  <span>${item.name}</span>" +
                                "  <a href=\"${item.resumeLink}\" target=\"_blank\" style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-primary-color);\">" +
                                "    ${item.resumeLink}" +
                                "  </a>" +
                                "</vaadin-vertical-layout>")
                .withProperty("name", Applicant::getName)
                .withProperty("resumeLink", applicant ->
                        applicant.getResumeLink() != null ? applicant.getResumeLink() : "Ссылка отсутствует");
    }
    private static Renderer<Applicant> createProgressAndScoreRenderer(Map<Long, TestSession> sessionMap, TestResultService testResultService) {
        return LitRenderer.<Applicant>of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span style=\"font-weight: bold;\">Завершенность: ${item.completion}</span>"
                                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                                + "      Баллы: ${item.score}" +
                                "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("completion", applicant -> {
                    TestSession session = sessionMap.get(applicant.getId());
                    if (session != null) {
                        int questionsSize = session.getQuestions().size();
                        int currentIndex = session.getCurrentQuestionIndex();
                        if (questionsSize > 0) {
                            return (currentIndex * 100 / questionsSize) + "%";
                        } else if (questionsSize == currentIndex) {
                            return "100%";
                        }
                    }
                    return "0%";
                })
                .withProperty("score", applicant -> {
                    TestSession session = sessionMap.get(applicant.getId());
                    if (session != null) {
                        return testResultService.calcScore(session.getId()).toString();
                    }
                    return "0";
                });
    }
    private static Renderer<Applicant> createTimeRenderer(DateTimeFormatter formatter) {
        return LitRenderer.<Applicant>of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                                + "    <span style=\"font-weight: bold;\">Начало: ${item.startTime}</span>"
                                + "    <span style=\"font-weight: bold;\">Окончание: ${item.endTime}</span>"
                                + "    </span>"
                                + "  </vaadin-vertical-layout>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("startTime", applicant -> {
                    OffsetDateTime startTime = applicant.getStartTime();
                    return startTime != null ? startTime.format(formatter) : "Не указано";
                })
                .withProperty("endTime", applicant -> {
                    OffsetDateTime endTime = applicant.getEndTime();
                    return endTime != null ? endTime.format(formatter) : "Не указано";
                });
    }

}
