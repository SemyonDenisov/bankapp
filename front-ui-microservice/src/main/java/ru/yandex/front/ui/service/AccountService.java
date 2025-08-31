package ru.yandex.front.ui.service;

import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.yandex.front.ui.model.ChangePasswordDto;

@Service
public class AccountService {
    RestTemplate restTemplate;
    public AccountService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void changePassword(String password,String confirmPassword) {
        request("http://accounts-microservice/change-password",Void.class,HttpMethod.POST,new ChangePasswordDto(password,confirmPassword));
    }

    public <T> T request(String url, Class<T> tClass,HttpMethod method,Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());

        HttpEntity entity = null;
        if(method.equals(HttpMethod.POST)){
            entity=new HttpEntity<>(body,headers);
        }
        else if(method.equals(HttpMethod.GET)){
            entity=new HttpEntity<>(headers);
        }

        return restTemplate.exchange(url, method, entity, tClass).getBody();
    }

}
