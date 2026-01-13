package edu.uoc.epcsd.digital.domain;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DigitalSession {

    @NotNull
    private Long id;

    @NotNull
    private String email;
    @NotNull

    private String description;

    @NotNull
    @Builder.Default
    private DigitalStatus status = DigitalStatus.AVAILABLE;

}
