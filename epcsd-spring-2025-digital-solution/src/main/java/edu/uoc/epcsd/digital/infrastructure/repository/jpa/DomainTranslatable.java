package edu.uoc.epcsd.digital.infrastructure.repository.jpa;

public interface DomainTranslatable<T> {

    T toDomain();

}
