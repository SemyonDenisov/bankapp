package ru.yandex.front.ui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String email;
    private String username;

    public String getLogin(){
        return email;
    }
}
