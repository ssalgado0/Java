package edu.uoc.epcsd.digital.application.rest;

import edu.uoc.epcsd.digital.domain.DigitalItem;
import edu.uoc.epcsd.digital.domain.service.DigitalItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/internal/digitalItem")
@SuppressWarnings("unused")
public class DigitalItemInternalRESTController {

    private final DigitalItemService digitalItemService;

    @GetMapping("/{digitalItemId}/alert")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<DigitalItem> getDigitalItemByIdToAlert(@PathVariable @NotNull Long digitalItemId) {
        log.trace("getDigitalItemById");
        return digitalItemService.getDigitalItemById(digitalItemId).map(item -> ResponseEntity.ok().body(item))
                .orElse(ResponseEntity.notFound().build());
    }

}
