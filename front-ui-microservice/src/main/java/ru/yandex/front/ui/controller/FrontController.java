package ru.yandex.front.ui.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.front.ui.service.TransferService;

@Controller
public class FrontController {

    TransferService cashService;

    public FrontController(TransferService cashService) {
        this.cashService = cashService;
    }

    @GetMapping("/")
    public String mainPage() {
        return "main";
    }

}
