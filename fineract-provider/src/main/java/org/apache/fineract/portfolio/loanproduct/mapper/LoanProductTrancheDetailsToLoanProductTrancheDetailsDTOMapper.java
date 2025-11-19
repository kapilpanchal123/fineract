package org.apache.fineract.portfolio.loanproduct.mapper;

import org.apache.fineract.infrastructure.core.config.MapstructMapperConfig;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductTrancheDetailsDTO;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductTrancheDetails;
import org.mapstruct.Mapper;

@Mapper(config = MapstructMapperConfig.class)
public interface LoanProductTrancheDetailsToLoanProductTrancheDetailsDTOMapper {

    LoanProductTrancheDetailsDTO fromLoanProductTrancheDetails(LoanProductTrancheDetails loanProductTrancheDetails);

    LoanProductTrancheDetails toLoanProductTrancheDetails(LoanProductTrancheDetailsDTO loanProductTrancheDetailsDTO);
}
