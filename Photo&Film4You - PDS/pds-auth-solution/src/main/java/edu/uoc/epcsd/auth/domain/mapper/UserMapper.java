package edu.uoc.epcsd.auth.domain.mapper;

import edu.uoc.epcsd.auth.domain.User;
import edu.uoc.epcsd.auth.infrastructure.repository.rest.GetUserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  User toDomain(GetUserResponse response);
}
