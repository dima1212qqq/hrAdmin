package ru.dovakun.views.login;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.dovakun.views.GuestLayout;
import ru.dovakun.data.enums.Role;
import ru.dovakun.data.entity.User;
import ru.dovakun.repo.UserRepository;
import ru.dovakun.security.AuthenticatedUser;
import ru.dovakun.services.UserService;

import java.util.Collections;

@AnonymousAllowed
@PageTitle("Авторизация")
@Route(value = "login", layout = GuestLayout.class)
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginOverlay login = new LoginOverlay();
    private final AuthenticatedUser authenticatedUser;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public LoginView(AuthenticatedUser authenticatedUser, UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder){
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        LoginI18n i18n = new LoginI18n();
        LoginI18n.Header header = new LoginI18n.Header();
        header.setTitle("HR ADMIN");
        header.setDescription("Админ панель для тестовых задач для HR");
        i18n.setHeader(header);
        LoginI18n.Form form = new LoginI18n.Form();
        form.setTitle("Авторизация");
        form.setUsername("E-mail");
        form.setPassword("Пароль");
        form.setSubmit("Войти");
        form.setForgotPassword("Забыли пароль?");
        i18n.setForm(form);
        LoginI18n.ErrorMessage error = new LoginI18n.ErrorMessage();
        error.setTitle("Проверьте введенные данные");
        error.setMessage("Попробуйте ещё раз или восстановите пароль");
        error.setUsername("Пустое поле с e-mail");
        error.setPassword("Пустое поле с паролем");
        i18n.setErrorMessage(error);
        i18n.setAdditionalInformation(null);
        login.setI18n(i18n);
        Button register = new Button("Регистрация", event -> {
            openDialogForRegister();
        });
        register.setSizeFull();
        register.getElement().setAttribute("name", "register");
        login.getFooter().add(register);
        login.setAction("login");
        login.getElement().setAttribute("no-autofocus", "");
        login.setOpened(true);
        login.addForgotPasswordListener(event -> {
            openDialogForgerPassword();
        });
        add(login);
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
//        if(beforeEnterEvent.getLocation()
//                .getQueryParameters()
//                .getParameters()
//                .containsKey("error")) {
//            login.setError(true);
//        }
    }
    public void openDialogForgerPassword(){
        Dialog dialog = new Dialog();
        dialog.open();
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.setWidth("300px");
        EmailField emailField = new EmailField("E-mail");
        PasswordField passwordField = new PasswordField("Новый пароль");
        TextField confPassword = new TextField("Код подтверждения");
        confPassword.setVisible(false);
        Button register = new Button("Восстановить пароль", event -> {
            confPassword.setVisible(true);
            if (confPassword.getValue().equals("0000")){
                try {
                    User user = userService.findByUsername(emailField.getValue()).get();
                    user.setHashedPassword(passwordEncoder.encode(passwordField.getValue()));
                    userService.save(user);
                    dialog.close();
                    Notification.show("Успешное восстановление пароля", 3000, Notification.Position.BOTTOM_CENTER);
                }catch (Exception e){
                    e.printStackTrace();
                    Notification.show(e.getMessage());
                    dialog.close();
                    openDialogForRegister();
                }

            }
        });
        emailField.addValueChangeListener(event -> {
            if (userService.findByUsername(emailField.getValue()).isEmpty()){
                register.setEnabled(false);
                emailField.setHelperText("Пользователя с таким email не существует");
            }else {
                register.setEnabled(true);
                emailField.setHelperText(null);
            }
        });
        layout.add(emailField, passwordField, confPassword, register);
        dialog.add(layout);
    }
    public void openDialogForRegister(){
        Dialog dialog = new Dialog();
        dialog.open();
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.setWidth("300px");
        EmailField emailField = new EmailField("E-mail");
        PasswordField passwordField = new PasswordField("Пароль");
        TextField confPassword = new TextField("Код подтверждения");
        confPassword.setVisible(false);
        Button register = new Button("Зарегистрироваться", event -> {
            confPassword.setVisible(true);
            if (confPassword.getValue().equals("0000")){
                try {
                    authenticatedUser.register(
                            emailField.getValue(),
                            passwordField.getValue(),
                            Collections.singleton(Role.ADMIN));
                    dialog.close();
                    Notification.show("Регистрация прошла успешно!", 3000, Notification.Position.BOTTOM_CENTER);
                }catch (Exception e){
                    e.printStackTrace();
                    Notification.show(e.getMessage());
                    dialog.close();
                    openDialogForRegister();
                }

            }
        });
        emailField.addValueChangeListener(event -> {
            if (userService.findByUsername(emailField.getValue()).isPresent()){
                register.setEnabled(false);
                emailField.setHelperText("Пользователь с данным Email уже существует");
            }else {
                register.setEnabled(true);
                emailField.setHelperText(null);
            }
        });
        layout.add(emailField, passwordField, confPassword, register);
        dialog.add(layout);
    }
}
