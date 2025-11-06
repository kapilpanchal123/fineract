package org.apache.fineract.portfolio.loanproduct.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AccountingRuleType {

    NONE(1, "accountingRuleType.none", "No accounting"), //
    CASH_BASED(2, "accountingRuleType.cash", "Cash based accounting"), //
    ACCRUAL_PERIODIC(3, "accountingRuleType.accrual.periodic", "Periodic accrual accounting"), //
    ACCRUAL_UPFRONT(4, "accountingRuleType.accrual.upfront", "Upfront accrual accounting"); //

    @Getter
    private final Integer value;

    @Getter
    private final String code;

    @Getter
    private final String description;
}
