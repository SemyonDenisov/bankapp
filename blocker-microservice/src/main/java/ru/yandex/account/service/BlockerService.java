package ru.yandex.account.service;


import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class BlockerService {


    public boolean block() {
        double p = ThreadLocalRandom.current().nextDouble(0, 1);
        return p < 0.1;
    }
}
