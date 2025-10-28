package org.apache.fineract.portfolio.account.mapper;

import org.apache.fineract.infrastructure.core.config.MapstructMapperConfig;
import org.apache.fineract.portfolio.account.data.StandingInstructionUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapstructMapperConfig.class)
public interface StandingInstructionUpdateRequestMapper {

    StandingInstructionUpdateRequestMapper INSTANCE = Mappers.getMapper(StandingInstructionUpdateRequestMapper.class);

    // Copy everything from source to target
    StandingInstructionUpdateRequest copy(StandingInstructionUpdateRequest source);

    // Update existing target object with source values
    void updateFrom(@MappingTarget StandingInstructionUpdateRequest target, StandingInstructionUpdateRequest source);

    // Map everything except @JsonIgnore fields
    @Mapping(target = "commandParam", ignore = true)
    @Mapping(target = "standingInstructionId", ignore = true)
    StandingInstructionUpdateRequest toServiceRequest(StandingInstructionUpdateRequest source);
}
