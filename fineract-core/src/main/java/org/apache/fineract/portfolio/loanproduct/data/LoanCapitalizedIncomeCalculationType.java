package org.apache.fineract.portfolio.loanproduct.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.fineract.infrastructure.core.api.ApiFacingEnum;

@AllArgsConstructor
public enum LoanCapitalizedIncomeCalculationType implements ApiFacingEnum<LoanCapitalizedIncomeCalculationType> {

    FLAT("loanCapitalizedIncomeCalculationType.flat", "Flat");

    @Getter
    private final String code;

    @Getter
    private final String humanReadableName;
}
