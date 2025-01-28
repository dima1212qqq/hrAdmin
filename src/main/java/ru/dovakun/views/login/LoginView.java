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
import ru.dovakun.services.MailService;
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

    private final AuthenticatedUser authenticatedUser;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private String confirmationCode; // Генерируйте код динамически для реальных случаев




    public LoginView(AuthenticatedUser authenticatedUser, UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder, MailService mailService){
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        LoginI18n i18n = getLoginI18n();
        LoginOverlay login = new LoginOverlay();
        login.setI18n(i18n);
        Button register = new Button("Register", event -> {
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
        this.mailService = mailService;
    }

    private static LoginI18n getLoginI18n() {
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
        return i18n;
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
    public void openDialogForgerPassword() {
        Dialog dialog = new Dialog();
        dialog.open();
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.setWidth("300px");
        TextField confPassword = new TextField("Код подтверждения");
        EmailField emailField = new EmailField("E-mail");
        confirmationCode = createGenerateNumber();
        Button sendCode = new Button("Отправить код подтверждения", event -> {
            confPassword.setVisible(true);
            String email = emailField.getValue();
            mailService.sendEmail(email, "Восстановление пароля",
                    "<p>Ваш код подтверждения: <strong>" + confirmationCode + "</strong></p>");
            Notification.show("Код подтверждения отправлен на почту", 3000, Notification.Position.BOTTOM_CENTER);
        });

        PasswordField passwordField = new PasswordField("Новый пароль");
        confPassword.setVisible(false);

        Button restorePassword = new Button("Восстановить пароль", event -> {
            if (confPassword.getValue().equals(confirmationCode)) {
                try {
                    User user = userService.findByUsername(emailField.getValue()).get();
                    user.setHashedPassword(passwordEncoder.encode(passwordField.getValue()));
                    userService.save(user);
                    dialog.close();
                    Notification.show("Успешное восстановление пароля", 3000, Notification.Position.BOTTOM_CENTER);
                } catch (Exception e) {
                    e.printStackTrace();
                    Notification.show(e.getMessage());
                    dialog.close();
                }
            }
        });
        emailField.addValueChangeListener(event -> {
            if (userService.findByUsername(emailField.getValue()).isEmpty()){
                restorePassword.setEnabled(false);
                sendCode.setEnabled(false);
                emailField.setHelperText("Пользователь с данным Email уже не существует");
            }else {
                restorePassword.setEnabled(true);
                sendCode.setEnabled(true);
                emailField.setHelperText(null);
            }
        });
        layout.add(emailField, sendCode, passwordField, confPassword, restorePassword);
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
        confirmationCode = createGenerateNumber();
        Button sendCode = new Button("Отправить код подтверждения", event -> {
            confPassword.setVisible(true);
            String email = emailField.getValue();
            mailService.sendEmail(email, "Регистрация аккаунта",
                    "<p>Ваш код подтверждения: <strong>" + confirmationCode + "</strong></p>");
            Notification.show("Код подтверждения отправлен на почту", 3000, Notification.Position.BOTTOM_CENTER);
        });
        confPassword.setVisible(false);
        Button register = new Button("Зарегистрироваться", event -> {
            if (confPassword.getValue().equals(confirmationCode)){
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
                sendCode.setEnabled(false);
                emailField.setHelperText("Пользователь с данным Email уже существует");
            }else {
                register.setEnabled(true);
                sendCode.setEnabled(true);
                emailField.setHelperText(null);
            }
        });
        layout.add(emailField,sendCode, passwordField, confPassword, register);
        dialog.add(layout);
    }
    private String createGenerateNumber() {
        int min = 1000;
        int max = 9999;
        return String.valueOf((int) (Math.random() * (max - min + 1)) + min);
    }
}
