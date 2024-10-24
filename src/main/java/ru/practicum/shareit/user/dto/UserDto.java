package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = { "id" })
public class UserDto {

    private Long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String name;
}
