package com.yakubovskiy.project.service.mapper;

import com.yakubovskiy.project.dto.UserDto;
import com.yakubovskiy.project.entity.User;
import com.yakubovskiy.project.service.interfaces.MapperService;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements MapperService<User, UserDto> {

    @Override
    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .patronymic(user.getPatronymic())
                .email(user.getEmail())
                .status(user.getStatus())
                .role(user.getRole())
                .balance(user.getBalance())
                .build();
    }

    @Override
    public User toEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .surname(userDto.getSurname())
                .patronymic(userDto.getPatronymic())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .status(userDto.getStatus())
                .balance(userDto.getBalance())
                .build();
    }
}
