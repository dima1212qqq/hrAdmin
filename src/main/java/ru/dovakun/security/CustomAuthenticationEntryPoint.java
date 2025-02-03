package ru.dovakun.security;

import com.vaadin.flow.component.UI;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final Set<String> vaadinRoutes = new HashSet<>();

    public CustomAuthenticationEntryPoint() {
        vaadinRoutes.add("/login");
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String requestURI = request.getRequestURI();

        if (isVaadinRoute(requestURI)) {
            response.sendRedirect(requestURI);
        } else {
            response.sendRedirect("/not-found");
        }
    }

    private boolean isVaadinRoute(String uri) {
        return vaadinRoutes.contains(uri);
    }
}
