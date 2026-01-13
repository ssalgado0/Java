package edu.uoc.epcsd.user.infrastructure.repository.jpa;

import edu.uoc.epcsd.user.domain.User;
import edu.uoc.epcsd.user.domain.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock
    private SpringDataUserRepository jpaRepository;

    @InjectMocks
    private UserRepositoryImpl userRepositoryImpl;

    @Test
    void updateUser_whenUserExists_updatesEntityAndSaves() {
        User domainUser = new User();
        domainUser.setId(1L);
        domainUser.setFullName("New Name");
        domainUser.setEmail("new@example.com");
        domainUser.setPassword("encoded-password");
        domainUser.setPhoneNumber("666777888");
        domainUser.setRole(UserRole.ADMIN);

        UserEntity existingEntity = new UserEntity();
        existingEntity.setId(1L);
        existingEntity.setFullName("Old Name");
        existingEntity.setEmail("old@example.com");
        existingEntity.setPassword("old-password");
        existingEntity.setPhoneNumber("111111111");
        existingEntity.setRole(UserRole.USER);

        when(jpaRepository.findById(1L)).thenReturn(Optional.of(existingEntity));

        userRepositoryImpl.updateUser(domainUser);

        assertEquals("New Name", existingEntity.getFullName());
        assertEquals("new@example.com", existingEntity.getEmail());
        assertEquals("encoded-password", existingEntity.getPassword());
        assertEquals("666777888", existingEntity.getPhoneNumber());
        assertEquals(UserRole.ADMIN, existingEntity.getRole());

        verify(jpaRepository).save(existingEntity);
    }

    @Test
    void updateUser_whenUserDoesNotExist_throwsIllegalArgumentException() {
        User domainUser = new User();
        domainUser.setId(99L);

        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userRepositoryImpl.updateUser(domainUser)
        );

        assertEquals("User not found with id 99", ex.getMessage());
        verify(jpaRepository, never()).save(any());
    }
}
