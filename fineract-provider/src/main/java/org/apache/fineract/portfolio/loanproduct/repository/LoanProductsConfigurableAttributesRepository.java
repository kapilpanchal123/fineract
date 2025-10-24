package org.apache.fineract.portfolio.loanproduct.repository;

import org.apache.fineract.portfolio.loanproduct.domain.LoanProductConfigurableAttributes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoanProductsConfigurableAttributesRepository
        extends JpaRepository<LoanProductConfigurableAttributes, Long>, JpaSpecificationExecutor<LoanProductConfigurableAttributes> {}
