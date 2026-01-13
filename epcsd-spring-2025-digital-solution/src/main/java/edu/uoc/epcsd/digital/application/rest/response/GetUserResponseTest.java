package edu.uoc.epcsd.digital.application.rest.response;

import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetUserResponseTest {

    private Long id;

    private String fullName;

    private String email;

    private String phoneNumber;

}

