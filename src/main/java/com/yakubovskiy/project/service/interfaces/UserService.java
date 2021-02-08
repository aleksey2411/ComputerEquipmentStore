package com.yakubovskiy.project.service.interfaces;

import com.yakubovskiy.project.entity.User;
import com.yakubovskiy.project.enums.UserStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface UserService {
    List<User> findAllUsers();

    User findUserById(User user, UUID userId);

    User findUserByEmail(String email);

    User registration(User user);

    List<User> findUsersByStatus(UserStatus userStatus);

    User blockUser(UUID userId);

    User unblockUser(UUID userId);

    User changePassword(UUID userId, String password);

    User fillUpBalance(UUID userId, Double moneyAmount);
}
