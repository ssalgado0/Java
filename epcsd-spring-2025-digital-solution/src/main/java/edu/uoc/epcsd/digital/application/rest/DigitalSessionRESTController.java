package edu.uoc.epcsd.digital.application.rest;

import edu.uoc.epcsd.digital.application.rest.request.CreateDigitalSessionRequest;
import edu.uoc.epcsd.digital.domain.DigitalSession;
import edu.uoc.epcsd.digital.domain.service.DigitalSessionService;
import edu.uoc.epcsd.digital.infrastructure.security.model.CurrentUser;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/digital")
@SuppressWarnings("unused")
public class DigitalSessionRESTController {

    private final DigitalSessionService digitalSessionService;

    @GetMapping("/allDigital")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<DigitalSession> getAllDigitalSession() {
        log.trace("getAllDigitalSession");

        return digitalSessionService.findAllDigitalSession();
    }

    @GetMapping("/{digitalSessionId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') and @securityService.hasAccessToDigitalSession(#digitalSessionId, #currentUser)")
    public ResponseEntity<DigitalSession> getDigitalSessionById(
        @PathVariable @NotNull Long digitalSessionId,
        @AuthenticationPrincipal CurrentUser currentUser) {
        log.trace("getDigitalItemById");

        return digitalSessionService.getDigitalSessionById(digitalSessionId).map(session -> ResponseEntity.ok().body(session))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public List<DigitalSession> findDigitalSessionByUser(
        @AuthenticationPrincipal CurrentUser currentUser) {
        log.trace("getDigitalSessionsByUser");
        return digitalSessionService.findDigitalSessionByUser(currentUser.getEmail());
    }

    @GetMapping("/digitalByUser")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<DigitalSession> findDigitalSessionByUserEmail(
        @RequestParam @NotBlank @Email String email) {
        log.trace("getDigitalSessionsByUserEmail");
        return digitalSessionService.findDigitalSessionByUser(email);
    }

    @PostMapping("/createDigital")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') and #createDigitalSessionRequest.getEmail().equals(#currentUser.getEmail())")
    public ResponseEntity<Long> createDigitalSession(
        @RequestBody @Valid CreateDigitalSessionRequest createDigitalSessionRequest,
        @AuthenticationPrincipal CurrentUser currentUser) {
        log.trace("createDigitalSession");

        log.trace("Creating DigitalSession " + createDigitalSessionRequest);
        Long digitalSessionId = digitalSessionService.createDigitalSession(DigitalSession.builder()
            .email(createDigitalSessionRequest.getEmail())
            .description(createDigitalSessionRequest.getDescription())
            .build());
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{userId}")
            .buildAndExpand(digitalSessionId)
            .toUri();

        return ResponseEntity.created(uri).body(digitalSessionId);
    }

    @PutMapping("/updateDigital/{digitalSessionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') and #updateDigitalSessionRequest.getEmail().equals(#currentUser.getEmail()) and @securityService.hasAccessToDigitalSession(#digitalSessionId, #currentUser)")
    public ResponseEntity<Boolean> updateDigitalSession(
        @PathVariable @NotNull Long digitalSessionId,
        @RequestBody @Valid CreateDigitalSessionRequest updateDigitalSessionRequest,
        @AuthenticationPrincipal CurrentUser currentUser) {
        log.trace("updateDigitalSession");
        log.info(digitalSessionId);
        log.trace("Updating DigitalSession {}", updateDigitalSessionRequest);
        digitalSessionService.updateDigitalSession(digitalSessionId,
            updateDigitalSessionRequest.getEmail(), updateDigitalSessionRequest.getDescription());

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @DeleteMapping("/removeDigital/{digitalSessionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') and @securityService.hasAccessToDigitalSession(#digitalSessionId, #currentUser)")
    public ResponseEntity<Boolean> removeDigitalSession(
        @PathVariable @NotNull Long digitalSessionId,
        @AuthenticationPrincipal CurrentUser currentUser) {
        log.trace("dropDigitalSession");
        log.info(digitalSessionId);

        digitalSessionService.removeDigitalSession(digitalSessionId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }    
}
