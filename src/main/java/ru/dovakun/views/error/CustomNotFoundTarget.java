package ru.dovakun.views.error;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import ru.dovakun.views.GuestLayout;
import ru.dovakun.views.MainLayout;

@Tag(Tag.DIV)
@ParentLayout(GuestLayout.class)
@PermitAll
public class CustomNotFoundTarget extends Component implements HasErrorParameter<NotFoundException> {

    @Override
    public int setErrorParameter(BeforeEnterEvent event,
                                 ErrorParameter<NotFoundException> parameter) {
        getElement().setText("Could not navigate to '"
                + event.getLocation().getPath()
                + "'");
        return HttpServletResponse.SC_NOT_FOUND;
    }
}

