package org.apache.fineract.portfolio.loanproduct.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum LoanScheduleProcessingType {

    HORIZONTAL("Horizontal"), //
    VERTICAL("Vertical"); //

    @Getter
    private final String humanReadableName;
}
