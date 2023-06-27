package ru.bfad.bfaApp.webComponent.dto;

import lombok.Data;

@Data
public class JwtRequest {
    private String username;
    private String password;

}
