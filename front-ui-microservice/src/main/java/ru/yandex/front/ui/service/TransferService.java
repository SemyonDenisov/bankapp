package ru.yandex.front.ui.service;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TransferService {

    RestTemplate restTemplate;

    public TransferService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

}
