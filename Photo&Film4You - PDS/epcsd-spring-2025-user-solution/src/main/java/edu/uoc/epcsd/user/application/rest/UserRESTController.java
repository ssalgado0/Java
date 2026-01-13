package edu.uoc.epcsd.user.application.rest;

import edu.uoc.epcsd.user.application.rest.request.CreateUserRequest;
import edu.uoc.epcsd.user.application.rest.response.GetUserResponse;
import edu.uoc.epcsd.user.domain.User;
import edu.uoc.epcsd.user.domain.service.UserService;
import edu.uoc.epcsd.user.domain.enums.UserRole;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import edu.uoc.epcsd.user.application.rest.request.UpdateUserRequest;
import edu.uoc.epcsd.user.application.rest.request.ChangePasswordRequest;
import edu.uoc.epcsd.user.infrastructure.security.model.CurrentUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.Map;


@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/users")
public class UserRESTController {

    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        log.trace("getAllUsers");

        return userService.findAllUsers();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GetUserResponse> getUserById(@PathVariable @NotNull Long userId) {
        log.trace("getUserById");

        return userService.findUserById(userId).map(user -> ResponseEntity.ok().body(GetUserResponse.fromDomain(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/byEmail/{email}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GetUserResponse> getUserByEmail(@PathVariable @NotNull String email) {
        log.trace("getUserByEmail");

        return userService.findUserByEmail(email).map(user -> ResponseEntity.ok().body(GetUserResponse.fromDomain(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Long> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        log.trace("createUser");
        log.trace("Creating user " + createUserRequest);

        Long userId = userService.createUser(User.builder()
                .email(createUserRequest.getEmail())
                .fullName(createUserRequest.getFullName())
                .phoneNumber(createUserRequest.getPhoneNumber())
                .password(createUserRequest.getPassword())
                .role(createUserRequest.getRole() != null ? createUserRequest.getRole() : UserRole.USER)
                .build());

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userId)
                .toUri();

        return ResponseEntity.created(uri).body(userId);
    }

    @GetMapping("/me")
    public ResponseEntity<GetUserResponse> getCurrentUser(
            @AuthenticationPrincipal CurrentUser currentUser) {

        log.trace("getCurrentUser");

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = Long.valueOf(currentUser.getId());

        return userService.findUserById(userId)
                .map(user -> ResponseEntity.ok(GetUserResponse.fromDomain(user)))
                .orElse(ResponseEntity.notFound().build());
    }




    @PutMapping("/me")
    public ResponseEntity<GetUserResponse> updateCurrentUser(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody @Valid UpdateUserRequest updateUserRequest) {

        log.trace("updateCurrentUser");

        Long userId = Long.valueOf(currentUser.getId());

        User updated = userService.updateUserProfile(
                userId,
                updateUserRequest.getFullName(),
                updateUserRequest.getPhoneNumber()
        );

        return ResponseEntity.ok(GetUserResponse.fromDomain(updated));
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<Object> changePassword(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest) {

        log.trace("changePassword");

        Long userId = Long.valueOf(currentUser.getId());

        try {
            userService.changePassword(
                    userId,
                    changePasswordRequest.getCurrentPassword(),
                    changePasswordRequest.getNewPassword()
            );

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);

        } catch (ResponseStatusException ex) {
            return ResponseEntity
                    .status(ex.getStatus())
                    .body(Map.of("message", ex.getReason()));
        }
    }


    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentUser(@AuthenticationPrincipal CurrentUser currentUser) {
        log.trace("deleteCurrentUser");

        Long userId = Long.valueOf(currentUser.getId());

        userService.deleteUser(userId);
    }
}
