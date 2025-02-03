package ru.dovakun.views.error;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.servlet.http.HttpServletResponse;
import ru.dovakun.views.GuestLayout;

@Route(value = "not-found", layout = GuestLayout.class)
@AnonymousAllowed
public class NotFoundView extends Div implements HasErrorParameter<NotFoundException> {

    public NotFoundView() {
        add(new H1("Ошибка 404: Страница не найдена"));
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        return HttpServletResponse.SC_NOT_FOUND;
    }
}

