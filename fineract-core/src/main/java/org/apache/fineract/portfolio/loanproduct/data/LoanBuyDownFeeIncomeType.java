package org.apache.fineract.portfolio.loanproduct.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.fineract.infrastructure.core.api.ApiFacingEnum;

@AllArgsConstructor
public enum LoanBuyDownFeeIncomeType implements ApiFacingEnum<LoanBuyDownFeeIncomeType> {

    FEE("buyDownFee.incomeType.fee", "Fee"), //
    INTEREST("buyDownFee.incomeType.interest", "Interest"); //

    @Getter
    private final String code;

    @Getter
    private final String humanReadableName;
}
