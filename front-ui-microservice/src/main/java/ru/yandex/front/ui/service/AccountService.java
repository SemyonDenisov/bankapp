package ru.yandex.front.ui.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.yandex.front.ui.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {
    RestTemplate restTemplate;
    ClientCredentialService clientCredentialService;
    CircuitBreaker circuitBreaker;
    Retry retry;
    LogService log;


    public AccountService(RestTemplate restTemplate, ClientCredentialService clientCredentialService,LogService logService) {
        this.restTemplate = restTemplate;
        this.clientCredentialService = clientCredentialService;
        circuitBreaker = CircuitBreaker.ofDefaults("accounts-microservice");
        retry = Retry.ofDefaults("accounts-microservice");
        this.log = logService;
    }

    public void changePassword(String password, String confirmPassword) {
        try {
            request("http://api-gateway/accounts/users/change-password", Void.class, HttpMethod.POST, new ChangePasswordDto(password, confirmPassword));
        }catch (Exception e){
            log.error("Ошибка при смене пароля");
        }
    }

    public void editAccount(String name, LocalDate birthDate, List<Currency> selectedCurrencies) {
        try {
            request("http://api-gateway/accounts/users/edit", Void.class, HttpMethod.POST, new UpdateUserDto(name, birthDate, selectedCurrencies));
        }
        catch (Exception e){
            log.error("Ошибка изменения данных");
        }
    }

    public List<Account> getAccounts() {
        try {
            HttpHeaders headers = new HttpHeaders();

            headers.setBearerAuth(SecurityContextHolder.getContext().getAuthentication().getDetails().toString());

            HttpEntity<String> entity = new HttpEntity<>(headers);

            return retry.executeSupplier(() -> circuitBreaker.executeSupplier(() -> restTemplate.exchange("http://accounts-microservice/accounts", HttpMethod.GET, entity, new ParameterizedTypeReference<List<Account>>() {
            }).getBody()));
        }catch (Exception e){
            log.error("Ошибка при запросе аккаунтов");
            return new ArrayList<>();
        }
    }


    public <T> T request(String url, Class<T> tClass, HttpMethod method, Object body) {
        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(SecurityContextHolder.getContext().getAuthentication().getDetails().toString());

        HttpEntity entity;
        if (method.equals(HttpMethod.POST)) {
            entity = new HttpEntity<>(body, headers);
        } else if (method.equals(HttpMethod.GET)) {
            entity = new HttpEntity<>(headers);
        } else {
            entity = null;
        }

        return retry.executeSupplier(()->circuitBreaker.executeSupplier(() -> restTemplate.exchange(url, method, entity, tClass).getBody()));

    }

    public List<User> getUsers() {
        try {
            HttpHeaders headers = new HttpHeaders();

            headers.setBearerAuth(SecurityContextHolder.getContext().getAuthentication().getDetails().toString());

            HttpEntity<String> entity = new HttpEntity<>(headers);
            return retry.executeSupplier(() -> circuitBreaker.executeSupplier(() -> restTemplate.exchange("http://api-gateway/accounts/users", HttpMethod.GET, entity, new ParameterizedTypeReference<List<User>>() {
            }).getBody()));
        }catch (Exception e){
            log.error("Ошибка при запросе пользователей");
            return new ArrayList<>();
        }
    }

    public Boolean registration(RegistrationForm form) {
        try {
            HttpHeaders headers = new HttpHeaders();

            headers.setBearerAuth(clientCredentialService.getToken());

            HttpEntity<RegistrationForm> entity = new HttpEntity<>(form, headers);
            retry.executeSupplier(() -> circuitBreaker.executeSupplier(() -> restTemplate.exchange("http://api-gateway/accounts/registration", HttpMethod.POST, entity, Object.class)));
            return true;
        }
        catch (Exception e){
            log.error("Ошибка во время регистрации");
            return false;
        }
    }

}
