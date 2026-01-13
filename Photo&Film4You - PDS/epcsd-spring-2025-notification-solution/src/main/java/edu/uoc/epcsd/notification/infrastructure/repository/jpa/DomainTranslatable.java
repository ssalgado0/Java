package edu.uoc.epcsd.notification.infrastructure.repository.jpa;

public interface DomainTranslatable<T> {

    T toDomain();

}
