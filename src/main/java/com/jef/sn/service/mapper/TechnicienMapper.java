package com.jef.sn.service.mapper;

import com.jef.sn.domain.ServiceJef;
import com.jef.sn.domain.Technicien;
import com.jef.sn.domain.User;
import com.jef.sn.service.dto.ServiceJefDTO;
import com.jef.sn.service.dto.TechnicienDTO;
import com.jef.sn.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Technicien} and its DTO {@link TechnicienDTO}.
 */
@Mapper(componentModel = "spring")
public interface TechnicienMapper extends EntityMapper<TechnicienDTO, Technicien> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "serviceJefs", source = "serviceJefs", qualifiedByName = "serviceJefIdSet")
    TechnicienDTO toDto(Technicien s);

    @Mapping(target = "removeServiceJefs", ignore = true)
    Technicien toEntity(TechnicienDTO technicienDTO);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("serviceJefId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ServiceJefDTO toDtoServiceJefId(ServiceJef serviceJef);

    @Named("serviceJefIdSet")
    default Set<ServiceJefDTO> toDtoServiceJefIdSet(Set<ServiceJef> serviceJef) {
        return serviceJef.stream().map(this::toDtoServiceJefId).collect(Collectors.toSet());
    }
}
