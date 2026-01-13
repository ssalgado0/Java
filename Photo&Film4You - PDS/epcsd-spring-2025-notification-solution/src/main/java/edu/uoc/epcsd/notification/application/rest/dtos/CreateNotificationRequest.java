package edu.uoc.epcsd.notification.application.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public final class CreateNotificationRequest {

    @NotNull
    private final Long userId;

    @NotNull
    private final String title;

    @NotNull
    private final String message;

    private final String type; // info, warning, success, error
}
