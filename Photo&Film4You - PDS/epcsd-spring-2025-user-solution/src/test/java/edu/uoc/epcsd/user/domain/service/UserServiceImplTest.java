package edu.uoc.epcsd.user.domain.service;

import edu.uoc.epcsd.user.domain.User;
import edu.uoc.epcsd.user.domain.exception.UserNotFoundException;
import edu.uoc.epcsd.user.domain.repository.AlertRepository;
import edu.uoc.epcsd.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFullName("Old Name");
        user.setPhoneNumber("111111111");
        user.setPassword("encoded-old-password");
        user.setEmail("old@example.com");
    }

    @Test
    void findAllUsers_returnsListFromRepository() {
        List<User> users = List.of(user);
        when(userRepository.findAllUsers()).thenReturn(users);

        List<User> result = userService.findAllUsers();

        assertEquals(users, result);
    }

    @Test
    void findUserById_delegatesToRepository() {
        when(userRepository.findUserById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void findUserByEmail_delegatesToRepository() {
        when(userRepository.findUserByEmail("old@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findUserByEmail("old@example.com");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void createUser_whenEmailDoesNotExist_encodesPasswordAndDelegatesToRepository() {
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setFullName("New Name");
        newUser.setPhoneNumber("222222222");
        newUser.setPassword("plain-password");

        when(userRepository.findUserByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(userRepository.createUser(any(User.class))).thenReturn(42L);

        Long id = userService.createUser(newUser);

        assertEquals(42L, id);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).createUser(captor.capture());
        assertEquals("encoded-password", captor.getValue().getPassword());

        verify(passwordEncoder).encode("plain-password");
        verify(userRepository).findUserByEmail("new@example.com");
    }

    @Test
    void createUser_whenEmailAlreadyExists_throwsConflict409() {
        User newUser = new User();
        newUser.setEmail("dup@example.com");
        newUser.setPassword("plain-password");

        when(userRepository.findUserByEmail("dup@example.com"))
                .thenReturn(Optional.of(new User()));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.createUser(newUser));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertEquals("Ya existe un usuario con ese email", ex.getReason());

        verify(userRepository, never()).createUser(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updateUserProfile_whenUserExists_updatesAndReturnsUser() {
        when(userRepository.findUserById(1L)).thenReturn(Optional.of(user));

        User result = userService.updateUserProfile(1L, "New Name", "222222222");

        assertEquals("New Name", result.getFullName());
        assertEquals("222222222", result.getPhoneNumber());
        verify(userRepository).updateUser(user);
    }

    @Test
    void updateUserProfile_whenUserDoesNotExist_throwsUserNotFoundException() {
        when(userRepository.findUserById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUserProfile(1L, "New Name", "222222222"));
    }

    @Test
    void changePassword_whenDataIsValid_updatesPassword() {
        when(userRepository.findUserById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("current1", "encoded-old-password")).thenReturn(true);
        when(passwordEncoder.encode("newpassword")).thenReturn("encoded-new-password");

        userService.changePassword(1L, "current1", "newpassword");

        assertEquals("encoded-new-password", user.getPassword());
        verify(userRepository).updateUser(user);
    }

    @Test
    void changePassword_whenCurrentPasswordIsIncorrect_throwsForbidden() {
        when(userRepository.findUserById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded-old-password")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.changePassword(1L, "wrong", "newpassword"));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void changePassword_whenNewPasswordTooShort_throwsBadRequest() {
        when(userRepository.findUserById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("current", "encoded-old-password")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.changePassword(1L, "current", "short"));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void changePassword_whenUserDoesNotExist_throwsUserNotFoundException() {
        when(userRepository.findUserById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.changePassword(1L, "current", "newpassword"));
    }

    @Test
    void deleteUser_delegatesToRepository() {
        userService.deleteUser(5L);
        verify(userRepository).deleteUser(5L);
    }

}
