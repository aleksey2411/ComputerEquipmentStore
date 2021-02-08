package com.yakubovskiy.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yakubovskiy.project.enums.UserRole;
import com.yakubovskiy.project.enums.UserStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.UUID;

@Builder
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto implements Serializable {
    @ApiModelProperty(position = 1, hidden = true)
    UUID id;
    @ApiModelProperty(position = 2)
    @NotBlank(message = "Enter your email")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9]+\\.[A-Za-z0-9]+$",
            message = "Enter a valid email address")
    String email;
    @ApiModelProperty(position = 3)
    @Length(min = 6, message = "Password must be at least 6 characters")
    String password;
    @ApiModelProperty(position = 4)
    @Pattern(regexp = "[A-ZА-Я][a-zа-я]*",
            message = "Enter a valid name")
    @NotBlank(message = "Enter your name")
    String name;
    @ApiModelProperty(position = 5)
    @Pattern(regexp = "[A-ZА-Я][a-zа-я]*",
            message = "Enter a valid surname")
    @NotBlank(message = "Enter your surname")
    String surname;
    @ApiModelProperty(position = 6)
    @Pattern(regexp = "[A-ZА-Я][a-zа-я]*",
            message = "Enter a valid patronymic")
    @NotBlank(message = "Enter your patronymic")
    String patronymic;
    @ApiModelProperty(position = 7, readOnly = true, hidden = true)
    UserStatus status;
    @ApiModelProperty(position = 8, readOnly = true, hidden = true)
    double balance;
    @ApiModelProperty(position = 9, readOnly = true, hidden = true)
    UserRole role;
}
