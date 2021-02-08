package com.yakubovskiy.project.repository;

import com.yakubovskiy.project.entity.User;
import com.yakubovskiy.project.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findUserByEmail(String email);

    List<User> findUsersByStatus(UserStatus status);

    Optional<User> findUserById(UUID userId);
}
