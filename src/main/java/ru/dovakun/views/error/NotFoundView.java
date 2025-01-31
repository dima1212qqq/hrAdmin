package ru.dovakun.views.error;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("not-found")
@PageTitle("Страница не найдена")
@AnonymousAllowed
public class NotFoundView extends VerticalLayout {

    public NotFoundView() {
        setAlignItems(Alignment.CENTER);
        add(new H1("Ошибка 404"), new Paragraph("Страница не найдена"));
    }
}

