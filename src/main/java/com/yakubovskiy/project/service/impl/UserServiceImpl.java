package com.yakubovskiy.project.service.impl;

import com.yakubovskiy.project.entity.Cart;
import com.yakubovskiy.project.entity.User;
import com.yakubovskiy.project.enums.UserRole;
import com.yakubovskiy.project.enums.UserStatus;
import com.yakubovskiy.project.exception.LogicException;
import com.yakubovskiy.project.exception.ResourceNotFoundException;
import com.yakubovskiy.project.repository.CartRepository;
import com.yakubovskiy.project.repository.UserRepository;
import com.yakubovskiy.project.service.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public UserServiceImpl(UserRepository userRepository, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    public List<User> findAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            log.error("Users not found.");
            throw new LogicException("There are not users in database.");
        }
        log.info("Users has been received.");
        return userRepository.findAll();
    }

    @Override
    public User findUserById(User curUser, UUID userId) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        if (curUser.getRole().equals(UserRole.USER)) {
            if (!user.getId().equals(curUser.getId())) {
                log.error("Error when trying to view someone else's user's info.");
                throw new LogicException("You can't watch other user's info.");
            }
        }
        log.info("{} has been received.", user);
        return user;
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
    }

    @Transactional
    @Override
    public User registration(User user) {
        if (!isEmailFree(user.getEmail())) {
            log.error("Error while registration. User with this email already exist.");
            throw new LogicException("Email is already in use.");
        }
        Cart cart = new Cart();
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ENABLE);
        user.setBalance(0.0);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        log.info("{} has been registered.", user);
        cart.setUser(user);
        cart.setGrandTotal(0.0);
        cartRepository.save(cart);
        log.info("{} has been created.", cart);
        return user;
    }

    @Override
    public List<User> findUsersByStatus(UserStatus userStatus) {
        List<User> users = userRepository.findUsersByStatus(userStatus);
        if (users.isEmpty()) {
            log.error("Users with status {} not found.", userStatus);
            throw new LogicException(" " + userStatus);
        }
        log.info("Users with status {} has been received.", userStatus);
        return users;
    }

    @Override
    public User blockUser(UUID userId) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        if (user.getStatus().equals(UserStatus.BLOCKED)) {
            log.error("Error while blocked {}. This user has been already blocked.", user);
            throw new LogicException("");
        }
        user.setStatus(UserStatus.BLOCKED);
        userRepository.saveAndFlush(user);
        log.info("{} has been blocked", user);
        return user;
    }

    @Override
    public User unblockUser(UUID userId) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        if (user.getStatus().equals(UserStatus.ENABLE)) {
            log.error("Error while unblocked {}. This user has been already enabled.", user);
            throw new LogicException("User has been already enabled");
        }
        user.setStatus(UserStatus.ENABLE);
        userRepository.saveAndFlush(user);
        log.info("{} has been unblocked", user);
        return user;
    }

    @Override
    public User changePassword(UUID userId, String password) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        String newPassword = bCryptPasswordEncoder.encode(password);
        if (user.getPassword().equals(newPassword)) {
            log.error("Error while changing password for {}. This password was previously used", user);
            throw new LogicException("This password was previously used");
        }
        user.setPassword(newPassword);
        userRepository.saveAndFlush(user);
        log.info("{} password has been changed.", user);
        return user;
    }

    @Override
    public User fillUpBalance(UUID userId, Double moneyAmount) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        user.setBalance(user.getBalance() + moneyAmount);
        userRepository.saveAndFlush(user);
        log.info("{} has been filled up balance.", user);
        return user;
    }

    private boolean isEmailFree(String email) {
        boolean isFree = true;
        Optional<User> user = userRepository.findUserByEmail(email);
        if (user.isPresent()) {
            isFree = false;
        }
        return isFree;
    }
}
