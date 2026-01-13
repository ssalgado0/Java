package edu.uoc.epcsd.notification.application.kafka;

import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DigitalItemMessage {

    private Long digitalItemId;

}