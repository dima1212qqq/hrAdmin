package ru.dovakun.views.main;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;
import ru.dovakun.data.entity.TestAssignment;
import ru.dovakun.data.entity.User;
import ru.dovakun.security.AuthenticatedUser;
import ru.dovakun.services.AnswerService;
import ru.dovakun.services.QuestionService;
import ru.dovakun.services.TestAssignmentService;
import ru.dovakun.views.MainLayout;

import java.util.List;

@PageTitle("Главная")
@Route(value = "",layout = MainLayout.class)
@Menu(order = 0, icon = LineAwesomeIconUrl.FILE)
@RolesAllowed("ADMIN")
public class MainView extends VerticalLayout {
    private final TestAssignmentService testAssignmentService;
    private final Grid<TestAssignment> testGrid;
    private final TestAssignmentForm testAssignmentForm;
    public TestAssignment currentTestAssignment;
    private final VerticalLayout layout;
    private static final int DESCRIPTION_MAX_LENGTH = 255;

    public MainView(TestAssignmentService testAssignmentService, AuthenticatedUser authenticatedUser, QuestionService questionService, AnswerService answerService) {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.START);
        User user = authenticatedUser.get().get();
        layout = new VerticalLayout();

        Button createTestAssignment = new Button("Создать тестовое", event -> {
            openDialogCreateTestAssignment(user);
        });
        testGrid = new Grid<>(TestAssignment.class, false);
        List<TestAssignment> testAssignments = testAssignmentService.getTestAssignmentsByUser(user.getId());
        testGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        testGrid.addColumn(createToggleDetailsRenderer(testGrid)).setWidth("80px")
                .setFlexGrow(0).setFrozen(true);
        testGrid.setItemDetailsRenderer(createTestAssignmentDetailsRenderer());
        testGrid.setPageSize(50);
        testGrid.addColumn(TestAssignment::getTitle).setHeader("Название тестового задания");
        testGrid.addColumn(TestAssignment::getVacancyLink).setHeader("Ссылка на вакансию");
        testGrid.setDetailsVisibleOnClick(false);
        testGrid.setItems(testAssignments);
        testAssignmentForm = new TestAssignmentForm(this, questionService, answerService, testAssignmentService, authenticatedUser);

        testGrid.addItemClickListener(event -> {
            currentTestAssignment = event.getItem();
            testAssignmentForm.setTestAssignment(currentTestAssignment);
            showForm(true);

        });
        layout.setFlexGrow(1, testGrid);
        layout.setFlexGrow(0, createTestAssignment);
        layout.expand(testGrid);
        layout.setSizeFull();
        layout.add(createTestAssignment, testGrid);
        add(layout);
        add(testAssignmentForm);
        testAssignmentForm.setVisible(false);
        this.testAssignmentService = testAssignmentService;
    }

    public void showForm(boolean isShow) {
        layout.setVisible(!isShow);
        testAssignmentForm.setVisible(isShow);
    }

    private void openDialogCreateTestAssignment(User user) {
        Dialog dialog = new Dialog();
        dialog.open();
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        TextField nameField = new TextField("Название тестового задания");
        TextField linkField = new TextField("Ссылка на вакансию");
        TextArea descriptionField = new TextArea("Описание");
        descriptionField.setMaxLength(DESCRIPTION_MAX_LENGTH);
        descriptionField.setValueChangeMode(ValueChangeMode.EAGER);
        descriptionField.addValueChangeListener(e -> {
            e.getSource()
                    .setHelperText(e.getValue().length() + "/" + "255");
        });
        Button save = new Button("Сохранить", event -> {
            if (nameField.isEmpty() || linkField.isEmpty()) {
                Notification.show("Заполните обязательные поля");
                return;
            }
            TestAssignment testAssignment = new TestAssignment();
            testAssignment.setTitle(nameField.getValue());
            testAssignment.setVacancyLink(linkField.getValue());
            testAssignment.setDescription(descriptionField.getValue());
            testAssignment.setUser(user);
            testAssignmentService.save(testAssignment);
            refreshGrid(user);
            dialog.close();
        });
        layout.add(nameField, linkField, descriptionField, save);
        dialog.add(layout);
    }

    private static Renderer<TestAssignment> createToggleDetailsRenderer(
            Grid<TestAssignment> grid) {

        return LitRenderer
                .<TestAssignment>of("""
                            <vaadin-button
                                theme="tertiary icon"
                                aria-label="Toggle details"
                                aria-expanded="${model.detailsOpened ? 'true' : 'false'}"
                                @click="${handleClick}"
                            >
                                <vaadin-icon
                                .icon="${model.detailsOpened ? 'lumo:angle-down' : 'lumo:angle-right'}"
                                ></vaadin-icon>
                            </vaadin-button>
                        """)
                .withFunction("handleClick",
                        testAssignment -> grid.setDetailsVisible(testAssignment,
                                !grid.isDetailsVisible(testAssignment)));
    }

    private static ComponentRenderer<TestAssignmentFormLayout, TestAssignment> createTestAssignmentDetailsRenderer() {
        return new ComponentRenderer<>(TestAssignmentFormLayout::new,
                TestAssignmentFormLayout::setTestAssignment);
    }

    private static class TestAssignmentFormLayout extends FormLayout {
        private final TextArea descriptionField = new TextArea("Описание");


        public TestAssignmentFormLayout() {
            descriptionField.setReadOnly(true);
            add(descriptionField);
        }

        public void setTestAssignment(TestAssignment testAssignment) {
            descriptionField.setValue(testAssignment.getDescription());
            descriptionField.setSizeFull();

        }
    }

    public void refreshGrid(User user) {
        testGrid.setItems(testAssignmentService.getTestAssignmentsByUser(user.getId()));
        testGrid.getDataProvider().refreshAll();
    }

}
