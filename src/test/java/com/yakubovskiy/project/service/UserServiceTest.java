package com.yakubovskiy.project.service;

import com.yakubovskiy.project.entity.User;
import com.yakubovskiy.project.enums.UserRole;
import com.yakubovskiy.project.enums.UserStatus;
import com.yakubovskiy.project.repository.CartRepository;
import com.yakubovskiy.project.repository.UserRepository;
import com.yakubovskiy.project.service.impl.UserServiceImpl;
import com.yakubovskiy.project.service.interfaces.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {UserServiceImpl.class})
public class UserServiceTest {

    private final UUID id = UUID.randomUUID();
    private final String email = "alex@mail.ru";
    private final String password = "qwerty123";
    private final List<User> list;
    private User inputUser;
    private User outputUser;

    public UserServiceTest() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodePassword = bCryptPasswordEncoder.encode(password);
        inputUser = User.builder()
                .id(id)
                .email(email)
                .balance(0.0)
                .role(UserRole.ADMIN)
                .password(encodePassword).build();
        outputUser = User.builder()
                .id(id)
                .email(email)
                .balance(0.0)
                .role(UserRole.ADMIN)
                .password(encodePassword).build();
        list = Arrays.asList(outputUser);
    }

    @Autowired
    private UserService userService;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testFindAll() {
        when(userRepository.findAll()).thenReturn(list);
        assertEquals(list, userService.findAllUsers());
    }

    @Test
    public void testFindById() {
        when(userRepository.findUserById(id)).thenReturn(Optional.of(inputUser));
        assertEquals(outputUser, userService.findUserById(inputUser, id));
    }

    @Test
    public void testFindByEmail() {
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(inputUser));
        assertEquals(outputUser, userService.findUserByEmail(email));
    }

    @Test
    public void testFindByStatus() {
        when(userRepository.findUsersByStatus(UserStatus.ENABLE)).thenReturn(list);
        assertEquals(list, userService.findUsersByStatus(UserStatus.ENABLE));
    }

    @Test
    public void testBlockUser() {
        inputUser.setStatus(UserStatus.BLOCKED);
        outputUser.setStatus(UserStatus.BLOCKED);
        when(userRepository.findUserById(id)).thenReturn(Optional.of(inputUser));

        assertThrows(NullPointerException.class, () -> {
            userService.blockUser(id);
        });
    }

    @Test
    public void testUnblockUser() {
        inputUser.setStatus(UserStatus.BLOCKED);
        outputUser.setStatus(UserStatus.ENABLE);
        when(userRepository.findUserById(id)).thenReturn(Optional.of(inputUser));
        assertEquals(outputUser, userService.unblockUser(id));
    }

    @Test
    public void testRegistration() {
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findUserById(id)).thenReturn(Optional.of(inputUser));
        when(userRepository.save(inputUser)).thenReturn(inputUser);
        userService.registration(inputUser);
        verify(userRepository, times(1)).save(inputUser);
    }

    @Test
    public void testChangePassword() {
        when(userRepository.findUserById(id)).thenReturn(Optional.of(inputUser));
        userService.changePassword(id, password);
        boolean isTrue = false;
        if (outputUser.getPassword().equals(inputUser.getPassword())) {
            isTrue = true;
        }
        assertFalse(isTrue);
        verify(userRepository, times(1)).findUserById(id);
    }

    @Test
    public void testFillUpBalance() {
        Double moneyAmount = 20.5;
        outputUser.setBalance(outputUser.getBalance() + moneyAmount);
        when(userRepository.findUserById(id)).thenReturn(Optional.of(inputUser));
        assertEquals(outputUser, userService.fillUpBalance(id, moneyAmount));
    }
}
