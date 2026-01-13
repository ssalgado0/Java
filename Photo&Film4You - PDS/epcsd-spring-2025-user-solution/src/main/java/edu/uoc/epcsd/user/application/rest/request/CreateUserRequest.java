package edu.uoc.epcsd.user.application.rest.request;

import edu.uoc.epcsd.user.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public final class CreateUserRequest {

    @NotBlank
    private final String fullName;

    @NotBlank
    private final String email;

    @NotBlank
    private final String password;

    @NotBlank
    private final String phoneNumber;

    @NotNull
    private final UserRole role;
}
