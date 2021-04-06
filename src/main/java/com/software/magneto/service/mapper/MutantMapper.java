package com.software.magneto.service.mapper;

import com.software.magneto.domain.Mutant;
import com.software.magneto.service.dto.MutantDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

//@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MutantMapper extends EntityMapper<MutantDTO, Mutant> {
}
