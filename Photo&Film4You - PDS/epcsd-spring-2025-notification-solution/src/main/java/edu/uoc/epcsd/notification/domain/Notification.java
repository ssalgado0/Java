package edu.uoc.epcsd.notification.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @NotNull
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String message;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @NotNull
    private Boolean read;

    private String type; // info, warning, success, error

    @NotNull
    private Long userId;

    private EntityReference entity = EntityReference.GENERAL;

    public enum EntityReference {
        DIGITAL_ITEM,
        GENERAL
    }
}
