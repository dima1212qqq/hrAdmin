package ru.dovakun.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.dovakun.data.entity.Role;
import ru.dovakun.data.entity.User;
import ru.dovakun.repo.UserRepository;

@Component
public class AuthenticatedUser {

    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;
    private final PasswordEncoder passwordEncoder;

    public AuthenticatedUser(AuthenticationContext authenticationContext, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(String email, String rawPassword, Set<Role> roles) {
        Optional<User> userOptional = userRepository.findByUsername(email);
        if (userOptional.isPresent()) {
            throw new IllegalArgumentException("Пользователь с данной почтой существует");
        }

        User user = new User();
        user.setUsername(email);
        user.setHashedPassword(passwordEncoder.encode(rawPassword));
        user.setRoles(roles);
        userRepository.save(user);
    }
    @Transactional
    public Optional<User> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(userDetails -> userRepository.findByUsername(userDetails.getUsername()).get());
    }

    public void logout() {
        authenticationContext.logout();
    }

}
