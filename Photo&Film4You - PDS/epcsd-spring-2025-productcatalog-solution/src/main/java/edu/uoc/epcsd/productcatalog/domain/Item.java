package edu.uoc.epcsd.productcatalog.domain;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @NotNull
    private String serialNumber;

    @NotNull
    @Builder.Default
    private ItemStatus status = ItemStatus.OPERATIONAL;

    @NotNull
    private Long productId;
}
