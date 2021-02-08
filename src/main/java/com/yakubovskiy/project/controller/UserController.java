package com.yakubovskiy.project.controller;

import com.yakubovskiy.project.dto.UserDto;
import com.yakubovskiy.project.entity.User;
import com.yakubovskiy.project.enums.UserStatus;
import com.yakubovskiy.project.exception.LogicException;
import com.yakubovskiy.project.service.interfaces.UserService;
import com.yakubovskiy.project.service.mapper.UserMapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/demo/v1")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @ApiOperation(
            value = "Find users",
            notes = "This method allows admin to view all users.")
    @GetMapping("/users/")
    public ResponseEntity<?> findAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList()));
    }

    @ApiOperation(
            value = "Find user by id",
            notes = "This method allows admin to view user account by id." +
                    "\n Also this method allows user to view his user account.")
    @GetMapping("/users/id/{userId}")
    public ResponseEntity<?> findUserById(@PathVariable final UUID userId) {
        User user = getCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDto(userService.findUserById(user, userId)));
    }

    @ApiOperation(
            value = "Fill up balance",
            notes = "This method allows user fill up him balance.")
    @PutMapping("/users/balance/{moneyAmount}")
    public ResponseEntity<?> fillUpBalance(@PathVariable final Double moneyAmount) {
        User user = getCurrentUser();
        return ResponseEntity.ok(userMapper.toDto(userService.fillUpBalance(user.getId(), moneyAmount)));
    }

    @ApiOperation(
            value = "Registration",
            notes = "This method allows anonymous users create account.")
    @PostMapping("/users/")
    public ResponseEntity<?> registration(@RequestBody final UserDto userDto) {
        Set<ConstraintViolation<UserDto>> violations = validate(userDto);
        for (ConstraintViolation<UserDto> violation : violations) {
            throw new LogicException("Incorrect data. Try again.\n" + violation.getMessage());
        }
        User user = userMapper.toEntity(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(userService.registration(user)));
    }

    @ApiOperation(
            value = "Change password",
            notes = "This method allows authorized users change his password.")
    @PutMapping("/users/passwords/{password}")
    public ResponseEntity<?> changePassword(@PathVariable final String password) {
        User user = getCurrentUser();
        return ResponseEntity.ok(userMapper.toDto(userService.changePassword(user.getId(), password)));
    }

    @ApiOperation(
            value = "Find blocked users",
            notes = "This method allows admin to view blocked users.")
    @GetMapping("/users/blocked-users")
    public ResponseEntity<?> findBlockedUsers() {
        return ResponseEntity.ok(userService.findUsersByStatus(UserStatus.BLOCKED).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList()));
    }

    @ApiOperation(
            value = "Find enabled users",
            notes = "This method allows admin to view enabled users.")
    @GetMapping("/users/enabled-users")
    public ResponseEntity<?> findEnabledUsers() {
        return ResponseEntity.ok(userService.findUsersByStatus(UserStatus.ENABLE).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList()));
    }

    @ApiOperation(
            value = "Find user by email",
            notes = "This method allows admin to find user by email.")
    @GetMapping("/users/search/{email}")
    public ResponseEntity<?> findUserByEmail(@PathVariable final String email) {
        return ResponseEntity.ok(userMapper.toDto(userService.findUserByEmail(email)));
    }

    @ApiOperation(
            value = "Block user",
            notes = "This method allows admin block user by id.")
    @PutMapping("/users/users-block-form/{userId}")
    public ResponseEntity<?> blockUserById(@PathVariable final UUID userId) {
        return ResponseEntity.ok(userMapper.toDto(userService.blockUser(userId)));
    }

    @ApiOperation(
            value = "Unblock user",
            notes = "This method allows admin unblock user by id.")
    @PutMapping("/users/users-unblock-form/{userId}")
    public ResponseEntity<?> unblockUserById(@PathVariable final UUID userId) {
        return ResponseEntity.ok(userMapper.toDto(userService.unblockUser(userId)));
    }

    private Set<ConstraintViolation<UserDto>> validate(UserDto userDto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(userDto);
    }

    private User getCurrentUser() {
        Authentication user = SecurityContextHolder.getContext()
                .getAuthentication();
        return userService.findUserByEmail(user.getName());
    }
}
