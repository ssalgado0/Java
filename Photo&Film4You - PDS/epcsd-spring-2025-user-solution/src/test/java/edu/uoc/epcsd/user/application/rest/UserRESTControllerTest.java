package edu.uoc.epcsd.user.application.rest;

import edu.uoc.epcsd.user.application.rest.request.ChangePasswordRequest;
import edu.uoc.epcsd.user.application.rest.request.UpdateUserRequest;
import edu.uoc.epcsd.user.application.rest.response.GetUserResponse;
import edu.uoc.epcsd.user.domain.User;
import edu.uoc.epcsd.user.domain.exception.UserNotFoundException;
import edu.uoc.epcsd.user.domain.service.UserService;
import edu.uoc.epcsd.user.infrastructure.security.model.CurrentUser;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRESTControllerTest {

    private final UserService userService = mock(UserService.class);
    private final UserRESTController controller = new UserRESTController(userService);

    @Test
    void changePassword_wrongCurrentPassword_returns403AndMessage() {
        CurrentUser currentUser = mock(CurrentUser.class);
        when(currentUser.getId()).thenReturn("1");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrong");
        request.setNewPassword("newValid123");

        doThrow(new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Current password is incorrect"
        )).when(userService).changePassword(1L, "wrong", "newValid123");

        ResponseEntity<?> response = controller.changePassword(currentUser, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Current password is incorrect", body.get("message"));
    }

    @Test
    void changePassword_ok_returns204NoContent() {
        CurrentUser currentUser = mock(CurrentUser.class);
        when(currentUser.getId()).thenReturn("1");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("current");
        request.setNewPassword("newValid123");

        ResponseEntity<?> response = controller.changePassword(currentUser, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(userService).changePassword(1L, "current", "newValid123");
    }

    @Test
    void changePassword_newPasswordTooShort_returns400AndMessage() {
        CurrentUser currentUser = mock(CurrentUser.class);
        when(currentUser.getId()).thenReturn("1");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("current");
        request.setNewPassword("short");

        doThrow(new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "New password is too short"
        )).when(userService).changePassword(1L, "current", "short");

        ResponseEntity<?> response = controller.changePassword(currentUser, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("New password is too short", body.get("message"));
    }

    @Test
    void updateCurrentUser_ok_updatesProfile() {
        CurrentUser currentUser = mock(CurrentUser.class);
        when(currentUser.getId()).thenReturn("1");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("New Name");
        request.setPhoneNumber("666777888");

        User domainUser = new User();
        domainUser.setId(1L);
        domainUser.setFullName("New Name");
        domainUser.setPhoneNumber("666777888");

        when(userService.updateUserProfile(1L, "New Name", "666777888"))
                .thenReturn(domainUser);

        ResponseEntity<GetUserResponse> response =
                controller.updateCurrentUser(currentUser, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        GetUserResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("New Name", body.getFullName());
        assertEquals("666777888", body.getPhoneNumber());
    }

    @Test
    void updateCurrentUser_userNotFound_throwsUserNotFoundException() {
        CurrentUser currentUser = mock(CurrentUser.class);
        when(currentUser.getId()).thenReturn("99");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("Any Name");
        request.setPhoneNumber("000000000");

        when(userService.updateUserProfile(99L, "Any Name", "000000000"))
                .thenThrow(new UserNotFoundException(99L));

        assertThrows(UserNotFoundException.class,
                () -> controller.updateCurrentUser(currentUser, request));
    }

    @Test
    void getAllUsers_returnsListFromService() {
        User u1 = new User();
        u1.setId(1L);
        User u2 = new User();
        u2.setId(2L);

        when(userService.findAllUsers()).thenReturn(List.of(u1, u2));

        List<User> result = controller.getAllUsers();

        assertEquals(2, result.size());
        assertSame(u1, result.get(0));
        assertSame(u2, result.get(1));
    }

    @Test
    void getUserById_whenUserExists_returnsOkAndBody() {
        User u = new User();
        u.setId(1L);
        u.setFullName("User One");

        when(userService.findUserById(1L)).thenReturn(Optional.of(u));

        ResponseEntity<GetUserResponse> response = controller.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        GetUserResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("User One", body.getFullName());
    }

    @Test
    void getUserById_whenUserDoesNotExist_returnsNotFound() {
        when(userService.findUserById(1L)).thenReturn(Optional.empty());

        ResponseEntity<GetUserResponse> response = controller.getUserById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getUserByEmail_whenUserExists_returnsOkAndBody() {
        User u = new User();
        u.setId(1L);
        u.setFullName("User One");

        when(userService.findUserByEmail("user@example.com")).thenReturn(Optional.of(u));

        ResponseEntity<GetUserResponse> response =
                controller.getUserByEmail("user@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        GetUserResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("User One", body.getFullName());
    }

    @Test
    void getUserByEmail_whenUserDoesNotExist_returnsNotFound() {
        when(userService.findUserByEmail("user@example.com")).thenReturn(Optional.empty());

        ResponseEntity<GetUserResponse> response =
                controller.getUserByEmail("user@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getCurrentUser_whenCurrentUserIsNull_returnsUnauthorized() {
        ResponseEntity<GetUserResponse> response = controller.getCurrentUser(null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getCurrentUser_whenUserExists_returnsOkAndBody() {
        CurrentUser currentUser = mock(CurrentUser.class);
        when(currentUser.getId()).thenReturn("1");

        User u = new User();
        u.setId(1L);
        u.setFullName("Current User");

        when(userService.findUserById(1L)).thenReturn(Optional.of(u));

        ResponseEntity<GetUserResponse> response =
                controller.getCurrentUser(currentUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        GetUserResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Current User", body.getFullName());
    }

    @Test
    void getCurrentUser_whenUserDoesNotExist_returnsNotFound() {
        CurrentUser currentUser = mock(CurrentUser.class);
        when(currentUser.getId()).thenReturn("1");

        when(userService.findUserById(1L)).thenReturn(Optional.empty());

        ResponseEntity<GetUserResponse> response =
                controller.getCurrentUser(currentUser);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }


    @Test
    void deleteCurrentUser_delegatesToService() {
        CurrentUser currentUser = mock(CurrentUser.class);
        when(currentUser.getId()).thenReturn("5");

        controller.deleteCurrentUser(currentUser);

        verify(userService).deleteUser(5L);
    }

}
