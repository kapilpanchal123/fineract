package org.apache.fineract.portfolio.loanproduct.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RepaymentStartDateType {

    INVALID(0, "repaymentStartDateType.invalid"), //
    DISBURSEMENT_DATE(1, "repaymentStartDateType.disbursementDate"), //
    SUBMITTED_ON_DATE(2, "repaymentStartDateType.submittedOnDate");

    @Getter
    private final Integer value;

    @Getter
    private final String code;
}
