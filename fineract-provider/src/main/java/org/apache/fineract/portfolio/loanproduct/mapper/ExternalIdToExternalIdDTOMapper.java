
package org.apache.fineract.portfolio.loanproduct.mapper;

import org.apache.fineract.infrastructure.core.config.MapstructMapperConfig;
import org.apache.fineract.infrastructure.core.domain.ExternalId;
import org.apache.fineract.portfolio.loanproduct.data.ExternalIdDTO;
import org.mapstruct.Mapper;

@Mapper(config = MapstructMapperConfig.class)
public interface ExternalIdToExternalIdDTOMapper {

    ExternalIdDTO fromExternalId(ExternalId externalId);

    ExternalId toExternalId(ExternalIdDTO externalIdDTO);
}
