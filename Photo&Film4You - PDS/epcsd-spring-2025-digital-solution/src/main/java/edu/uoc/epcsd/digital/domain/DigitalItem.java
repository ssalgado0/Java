package edu.uoc.epcsd.digital.domain;

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
public class DigitalItem {
	
    @NotNull
    private Long id;
    
    @NotNull
    private Long digitalsessionid;

    @NotNull
    private String description;
    
	@NotNull
  private Double lat;
    
    @NotNull
    private Double lon;
    
    @NotNull
    private String link;

    @NotNull
    @Builder.Default
    private DigitalStatus status = DigitalStatus.AVAILABLE;

}
