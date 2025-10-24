package org.apache.fineract.portfolio.loanproduct.repository;

import org.apache.fineract.portfolio.loanproduct.domain.LoanProductFloatingRates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoanProductsFloatingRatesRepository
        extends JpaRepository<LoanProductFloatingRates, Long>, JpaSpecificationExecutor<LoanProductFloatingRates> {}
