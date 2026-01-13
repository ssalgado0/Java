package edu.uoc.epcsd.digital.application.rest;

import edu.uoc.epcsd.digital.application.rest.request.CreateDigitalItemRequest;
import edu.uoc.epcsd.digital.domain.DigitalItem;
import edu.uoc.epcsd.digital.domain.service.DigitalItemService;
import edu.uoc.epcsd.digital.infrastructure.security.model.CurrentUser;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
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
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping("/digitalItem")
@SuppressWarnings("unused")
public class DigitalItemRESTController {

    private final DigitalItemService digitalItemService;

    @GetMapping("/allItems")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EXTERNAL')")
    public List<DigitalItem> getAllDigitalItem() {
        log.trace("getAllDigitalItem");
        return digitalItemService.findAllDigitalItem();
    }

    @GetMapping("/{digitalItemId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') and @securityService.hasAccessToDigitalItem(#digitalItemId, #currentUser)")
    public ResponseEntity<DigitalItem> getDigitalItemById(@PathVariable @NotNull Long digitalItemId,
        @AuthenticationPrincipal CurrentUser currentUser) {
        log.trace("getDigitalItemById");
        return digitalItemService.getDigitalItemById(digitalItemId).map(item -> ResponseEntity.ok().body(item))
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/digitalItemBySession")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') and @securityService.hasAccessToDigitalSession(#digitalSessionId, #currentUser)")
    public List<DigitalItem> findDigitalItemBySession(@RequestParam @NotNull Long digitalSessionId,
        @AuthenticationPrincipal CurrentUser currentUser) {
        log.trace("getDigitalItemsBySession");
        return digitalItemService.findDigitalItemBySession(digitalSessionId);
    }

    @GetMapping(value = "/digitalItemBySession", params = "count=true")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') and @securityService.hasAccessToDigitalSession(#digitalSessionId, #currentUser)")
    public Long countDigitalItemBySession(@RequestParam @NotNull Long digitalSessionId,
        @AuthenticationPrincipal CurrentUser currentUser) {
        log.trace("getDigitalItemsBySession");
        return digitalItemService.countDigitalItemBySession(digitalSessionId);
    }

    @PostMapping("/addItem")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') and @securityService.hasAccessToDigitalSession(#createDigitalItemRequest.getDigitalSessionId(), #currentUser)")
    public ResponseEntity<Long> addDigitalItem(
        @RequestBody @Valid CreateDigitalItemRequest createDigitalItemRequest,
        @AuthenticationPrincipal CurrentUser currentUser) {
        log.trace("addDigitalItem");

        log.trace("Adding DigitalItem {}", createDigitalItemRequest);
        Long digitalItemId = digitalItemService.addDigitalItem(DigitalItem.builder()
            .digitalsessionid(createDigitalItemRequest.getDigitalSessionId())
            .description(createDigitalItemRequest.getDescription())
            .lon(createDigitalItemRequest.getLon())
            .lat(createDigitalItemRequest.getLat())
            .link(createDigitalItemRequest.getLink())
            .build());
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(digitalItemId)
            .toUri();

        return ResponseEntity.created(uri).body(digitalItemId);
    }

    @PutMapping("/updateItem/{digitalItemId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') and @securityService.hasAccessToDigitalItem(#digitalItemId, #currentUser) and @securityService.hasAccessToDigitalSession(#updateDigitalItemRequest.getDigitalSessionId(), #currentUser)")
    public ResponseEntity<Boolean> updateDigitalItem(@PathVariable @NotNull Long digitalItemId,
        @RequestBody @Valid CreateDigitalItemRequest updateDigitalItemRequest,
        @AuthenticationPrincipal CurrentUser currentUser) {
        log.trace("updateDigitalItem");
        log.info(digitalItemId);

        log.trace("Updating DigitalItem {}", updateDigitalItemRequest);
        digitalItemService.updateDigitalItem(digitalItemId,
            updateDigitalItemRequest.getDescription(), updateDigitalItemRequest.getLink(),
            updateDigitalItemRequest.getLat(), updateDigitalItemRequest.getLon());

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PatchMapping("/reviewDigitalItem/{digitalItemId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EXTERNAL')")
    public ResponseEntity<Boolean> setDigitalItemForReview(@PathVariable @NotNull Long digitalItemId) {
        log.trace("setDigitalItemForReview");
        log.info(digitalItemId);

            digitalItemService.setDigitalItemForReview(digitalItemId);
            return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PatchMapping("/approveDigitalItem/{digitalItemId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Boolean> approvePendingDigitalItem(@PathVariable @NotNull Long digitalItemId) {
        log.trace("approvePendingDigitalItem");
        log.info(digitalItemId);

            digitalItemService.approvePendingDigitalItem(digitalItemId);
            return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PatchMapping("/rejectDigitalItem/{digitalItemId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Boolean> rejectPendingDigitalItem(@PathVariable @NotNull Long digitalItemId) {
        log.trace("rejectPendingDigitalItem");
        log.info(digitalItemId);

        digitalItemService.rejectPendingDigitalItem(digitalItemId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @DeleteMapping("/dropItem/{digitalItemId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') and @securityService.hasAccessToDigitalItem(#digitalItemId, #currentUser)")
    public ResponseEntity<Boolean> dropDigitalItem(@PathVariable @NotNull Long digitalItemId,
        @AuthenticationPrincipal CurrentUser currentUser) {
        log.trace("dropDigitalItem");
        log.info(digitalItemId);

        digitalItemService.dropDigitalItem(digitalItemId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }  
}
