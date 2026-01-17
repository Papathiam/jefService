package com.jef.sn.service.mapper;

import com.jef.sn.domain.ServiceJef;
import com.jef.sn.domain.Technicien;
import com.jef.sn.service.dto.ServiceJefDTO;
import com.jef.sn.service.dto.TechnicienDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ServiceJef} and its DTO {@link ServiceJefDTO}.
 */
@Mapper(componentModel = "spring")
public interface ServiceJefMapper extends EntityMapper<ServiceJefDTO, ServiceJef> {
    @Mapping(target = "techniciens", source = "techniciens", qualifiedByName = "technicienIdSet")
    ServiceJefDTO toDto(ServiceJef s);

    @Mapping(target = "techniciens", ignore = true)
    @Mapping(target = "removeTechniciens", ignore = true)
    ServiceJef toEntity(ServiceJefDTO serviceJefDTO);

    @Named("technicienId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TechnicienDTO toDtoTechnicienId(Technicien technicien);

    @Named("technicienIdSet")
    default Set<TechnicienDTO> toDtoTechnicienIdSet(Set<Technicien> technicien) {
        return technicien.stream().map(this::toDtoTechnicienId).collect(Collectors.toSet());
    }
}
