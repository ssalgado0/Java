package edu.uoc.epcsd.user.infrastructure.repository.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.uoc.epcsd.user.domain.User;
import edu.uoc.epcsd.user.domain.enums.UserRole;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "User")
@ToString
@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`user`")
public class UserEntity implements DomainTranslatable<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fullName", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phoneNumber", nullable = false)
    private String phoneNumber;

  @Column(name = "role", nullable = false)
  @Enumerated(EnumType.STRING)
  private UserRole role;

    @JsonIgnore
    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<AlertEntity> alerts;

    public static UserEntity fromDomain(User user) {
        if (user == null) {
            return null;
        }

        return UserEntity.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .password(user.getPassword())
                .phoneNumber(user.getPhoneNumber())
            .role(user.getRole())
                .build();
    }

    @Override
    public User toDomain() {
        return User.builder()
                .id(this.getId())
                .fullName(this.getFullName())
                .email(this.getEmail())
                .password(this.getPassword())
                .phoneNumber(this.getPhoneNumber())
            .role(this.getRole())
                .build();
    }
}
