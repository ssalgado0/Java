package edu.uoc.epcsd.productcatalog.application.rest;


import edu.uoc.epcsd.productcatalog.application.rest.request.CreateItemRequest;
import edu.uoc.epcsd.productcatalog.domain.Item;
import edu.uoc.epcsd.productcatalog.domain.service.ItemService;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/items")
public class ItemRESTController {

    private final ItemService itemService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Item> getAllItems() {
        log.trace("getAllItems");

        return itemService.findAllItems();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{serialNumber}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Item> getItemById(@PathVariable @NotBlank String serialNumber) {
        log.trace("getItemById");

        return itemService.findBySerialNumber(serialNumber).map(item -> ResponseEntity.ok().body(item))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/product/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Item> getItemsByProductId(@PathVariable @NotNull Long productId) {
        log.trace("getItemsByProductId");

        return itemService.findByProductId(productId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{serialNumber}")
    public ResponseEntity<Item> setOperationalItem(@PathVariable @NotBlank String serialNumber, @RequestBody @NotNull boolean operational) {

        try {
            return ResponseEntity.ok().body(itemService.setOperational(serialNumber, operational));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified item serial number '" + serialNumber + "' does not exist.");
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<String> createItem(@RequestBody @NotNull @Valid CreateItemRequest createItemRequest) {
        log.trace("createItem");

        try {
            log.trace("Creating item " + createItemRequest);
            String serialNumber = itemService.createItem(createItemRequest.getProductId(),
                    createItemRequest.getSerialNumber());
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{serialNumber}")
                    .buildAndExpand(serialNumber)
                    .toUri();

            return ResponseEntity.created(uri).body(serialNumber);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified product id " + createItemRequest.getProductId() + " does not exist.", e);
        }
    }
}
