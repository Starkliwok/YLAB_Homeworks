package com.Y_LAB.homework.model.dto.request;

import com.Y_LAB.homework.model.roles.User;
import lombok.*;

import java.io.Serializable;

/**
 * DTO для {@link User}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

    private String username;
    private String password;
}