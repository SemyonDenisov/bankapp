package ru.yandex.account.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ru.yandex.account.model.LoginForm;
import ru.yandex.account.model.RegistrationForm;
import ru.yandex.account.model.Token;
import ru.yandex.account.service.JwtService;
import ru.yandex.account.service.UserService;

import java.util.Objects;

@RestController
public class AuthController {
    UserService userService;
    AuthenticationManager authenticationManager;
    JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService,
                          JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<Token> login(@RequestBody LoginForm loginForm) {
        var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.getEmail(), loginForm.getPassword()));
        if (auth.isAuthenticated()) {
            String token = jwtService.generateToken(loginForm.getEmail());
            return new ResponseEntity<>(new Token(token), HttpStatus.OK);
        }
        throw new BadCredentialsException("Invalid email or password");
    }

    @PostMapping("/registration")
    public ResponseEntity<Token> registration(@RequestBody @Valid RegistrationForm registrationForm) {
        if (!Objects.equals(registrationForm.getPassword(), registrationForm.getConfirmPassword())) {
            throw new BadCredentialsException("Passwords do not match");
        }
        userService.saveNewUser(registrationForm);
        return login(new LoginForm(registrationForm.getEmail(), registrationForm.getPassword()));
    }


}
