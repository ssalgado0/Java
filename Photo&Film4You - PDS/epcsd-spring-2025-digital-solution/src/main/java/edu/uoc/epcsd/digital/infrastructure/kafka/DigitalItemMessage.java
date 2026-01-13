package edu.uoc.epcsd.digital.infrastructure.kafka;


import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
public class DigitalItemMessage {

    private final Long digitalItemId;

}
