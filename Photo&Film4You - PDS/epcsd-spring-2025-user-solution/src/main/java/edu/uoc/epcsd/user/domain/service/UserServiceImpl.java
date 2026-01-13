package edu.uoc.epcsd.user.domain.service;

import edu.uoc.epcsd.user.domain.User;
import edu.uoc.epcsd.user.domain.exception.UserNotFoundException;
import edu.uoc.epcsd.user.domain.repository.AlertRepository;
import edu.uoc.epcsd.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final AlertRepository alertRepository;

    private final PasswordEncoder passwordEncoder;

    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findUserById(id);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public Long createUser(User user) {
        userRepository.findUserByEmail(user.getEmail()).ifPresent(u -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese email");
        });

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.createUser(user);
    }

    public Set<User> getUsersToAlert(Long productId, LocalDate availableOnDate) {
        return alertRepository.findAlertsByProductAndDate(productId, availableOnDate).stream()
                .collect(Collectors.toList())
                .stream()
                .map(alert -> userRepository.findUserById(alert.getUserId()).get())
                .collect(Collectors.toSet());
    }

    @Override
    public User updateUserProfile(Long userId, String fullName, String phoneNumber) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setFullName(fullName);
        user.setPhoneNumber(phoneNumber);

        userRepository.updateUser(user);

        return user;
    }

    @Override
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La contraseña actual no es correcta");
        }

        if (newPassword == null || newPassword.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La nueva contraseña debe tener al menos 8 caracteres");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.updateUser(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }
}
