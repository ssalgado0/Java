package edu.uoc.epcsd.user.application.rest.response;

import edu.uoc.epcsd.user.domain.User;
import edu.uoc.epcsd.user.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
public final class GetUserResponse {

    private final Long id;

    private final String fullName;

    private final String email;

    private final String phoneNumber;

  private final UserRole role;

    public static GetUserResponse fromDomain(User user) {
        return GetUserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
            .role(user.getRole())
                .build();
    }
}
