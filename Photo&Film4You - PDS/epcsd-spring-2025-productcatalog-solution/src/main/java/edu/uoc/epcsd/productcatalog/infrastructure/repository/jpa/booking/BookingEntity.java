package edu.uoc.epcsd.productcatalog.infrastructure.repository.jpa.booking;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "Booking")
@ToString(exclude = "lines")
@Getter
@Setter
@EqualsAndHashCode(exclude = {"lines", "allocations"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private BookingStatus status;

  @OneToMany(mappedBy = "booking", orphanRemoval = true, fetch = FetchType.EAGER)
  @Default
  private Set<BookingLineEntity> lines = new HashSet<>();

  @OneToMany(mappedBy = "booking", orphanRemoval = true, fetch = FetchType.EAGER)
  @Default
  private Set<ItemAllocationEntity> allocations = new HashSet<>();
}

