package edu.uoc.epcsd.digital.infrastructure.repository.jpa;

import edu.uoc.epcsd.digital.domain.DigitalStatus;
import edu.uoc.epcsd.digital.domain.DigitalSession;
import lombok.*;

import javax.persistence.*;

@Entity(name = "digitalsession")
@ToString
@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DigitalSessionEntity implements DomainTranslatable<DigitalSession> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "email", nullable = false)
    private String email;
    
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DigitalStatus status = DigitalStatus.AVAILABLE;

    public static DigitalSessionEntity fromDomain(DigitalSession digitalSession) {
        if (digitalSession == null) {
            return null;
        }

        return DigitalSessionEntity.builder()
                .id(digitalSession.getId())
                .email(digitalSession.getEmail())                
                .description(digitalSession.getDescription())
                .status(digitalSession.getStatus())
                .build();
    }

    @Override
    public DigitalSession toDomain() {
        return DigitalSession.builder()
                .id(this.getId())
                .email(this.getEmail())
                .description(this.getDescription())
                .status(this.getStatus())
                .build();
    }
}
