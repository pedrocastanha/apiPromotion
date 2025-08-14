package org.example.domain.client;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClientMapper {
   ClientRecord.clientListDTO toClientListDTO(Client client);

   void updateClientFromDto(ClientRecord.updateClientDTO dto, @MappingTarget Client client);
}
