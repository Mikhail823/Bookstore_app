package com.example.bookshop.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class MessageFormDto {

    @NotBlank
    private String name;
    @Pattern(regexp = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", message = "Не соответствует формату email адреса")
    private String mail;
    @NotNull(message = "Введите название")
    private String topic;
    @NotBlank(message = "Введите сообщение")
    private String message;
}
