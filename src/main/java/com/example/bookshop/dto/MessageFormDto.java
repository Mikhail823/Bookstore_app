package com.example.bookshop.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class MessageFormDto {

    @NotBlank
    private String name;
    @Pattern(regexp = "\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}", message = "Не соответствует формату email адреса")
    private String mail;
    @NotNull(message = "Введите название")
    private String topic;
    @NotBlank(message = "Введите сообщение")
    private String message;
}
