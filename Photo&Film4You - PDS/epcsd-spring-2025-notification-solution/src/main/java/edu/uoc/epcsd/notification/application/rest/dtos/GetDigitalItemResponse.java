package edu.uoc.epcsd.notification.application.rest.dtos;


import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetDigitalItemResponse {

    private Long id;

    private Long digitalsessionid;

    private String description;

    private Double lat;

    private Double lon;

    private String link;

    private DigitalStatus status = DigitalStatus.AVAILABLE;

    public enum DigitalStatus {
        AVAILABLE,
        NOT_AVAILABLE,
        REVIEW_PENDING;
    }
}
