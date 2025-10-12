package ru.yandex.front.ui.service;

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

    public AccountService(RestTemplate restTemplate, ClientCredentialService clientCredentialService) {
        this.restTemplate = restTemplate;
        this.clientCredentialService = clientCredentialService;
    }

    public void changePassword(String password, String confirmPassword) {
        request("http://accounts-microservice/users/change-password", Void.class, HttpMethod.POST, new ChangePasswordDto(password, confirmPassword));
    }

    public void editAccount(String name, LocalDate birthDate, List<Currency> selectedCurrencies) {
        request("http://accounts-microservice/users/edit", Void.class, HttpMethod.POST, new UpdateUserDto(name, birthDate, selectedCurrencies));
    }

    public List<Account> getAccounts() {
        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(SecurityContextHolder.getContext().getAuthentication().getDetails().toString());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange("http://accounts-microservice/accounts", HttpMethod.GET, entity, new ParameterizedTypeReference<List<Account>>() {
        }).getBody();
    }

    public <T> T request(String url, Class<T> tClass, HttpMethod method, Object body) {
        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(SecurityContextHolder.getContext().getAuthentication().getDetails().toString());

        HttpEntity entity = null;
        if (method.equals(HttpMethod.POST)) {
            entity = new HttpEntity<>(body, headers);
        } else if (method.equals(HttpMethod.GET)) {
            entity = new HttpEntity<>(headers);
        }

        return restTemplate.exchange(url, method, entity, tClass).getBody();
    }

    public List<User> getUsers() {
        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(SecurityContextHolder.getContext().getAuthentication().getDetails().toString());

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange("http://accounts-microservice/users", HttpMethod.GET, entity, new ParameterizedTypeReference<List<User>>() {
        }).getBody();
    }

    public Boolean registration(RegistrationForm form) {
        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(clientCredentialService.getToken());

        HttpEntity<RegistrationForm> entity = new HttpEntity<>(form,headers);
        var a = restTemplate.exchange("http://accounts-microservice/registration", HttpMethod.POST, entity,Object.class);
        return true;
    }

}
