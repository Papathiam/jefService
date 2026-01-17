package com.jef.sn.service.mapper;

import com.jef.sn.domain.Client;
import com.jef.sn.domain.User;
import com.jef.sn.service.dto.ClientDTO;
import com.jef.sn.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Client} and its DTO {@link ClientDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClientMapper extends EntityMapper<ClientDTO, Client> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    ClientDTO toDto(Client s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
