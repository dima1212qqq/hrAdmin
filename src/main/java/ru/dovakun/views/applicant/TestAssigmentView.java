package ru.dovakun.views.applicant;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;
import ru.dovakun.data.entity.TestAssignment;
import ru.dovakun.security.AuthenticatedUser;
import ru.dovakun.services.TestAssignmentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Route("tests")
@PageTitle("Список заданий")
@RolesAllowed("ADMIN")
@Menu(order = 1, icon = LineAwesomeIconUrl.QUESTION_CIRCLE)
public class TestAssigmentView extends VerticalLayout {
    List<TestAssignment> testAssignments;
    Grid<TestAssignment> grid = new Grid<>(TestAssignment.class, false);

    public TestAssigmentView(TestAssignmentService testAssignmentService, AuthenticatedUser authenticatedUser) {
        testAssignments = testAssignmentService.getTestAssignmentsByUser(authenticatedUser.get().get().getId());
        grid.setItems(testAssignments);
        grid.addColumn(TestAssignment::getTitle).setHeader("Название вакансии").setResizable(true);
        grid.addColumn(TestAssignment::getVacancyLink).setHeader("Ссылка на вакансию").setResizable(true);
        grid.addItemClickListener(event -> {
            UI.getCurrent().navigate("applicants/" + event.getItem().getId());
        });
        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> findTest(e.getValue()));
        add(searchField, grid);
    }

    private void findTest(String value) {
        if (value != null && !value.isEmpty()) {}
        List<TestAssignment> newTestAssignments = new ArrayList<>();
        testAssignments.forEach(testAssignment -> {
            String title = testAssignment.getTitle().toLowerCase();
            if (title.contains(Objects.requireNonNull(value).toLowerCase())) {
                newTestAssignments.add(testAssignment);
            }
        });
        grid.setItems(newTestAssignments);
    }
}
