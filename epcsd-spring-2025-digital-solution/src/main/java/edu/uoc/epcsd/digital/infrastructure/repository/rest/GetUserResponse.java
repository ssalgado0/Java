package edu.uoc.epcsd.digital.infrastructure.repository.rest;

import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetUserResponse {

    private Long id;

    private String fullName;

    private String email;

    private String phoneNumber;

}
