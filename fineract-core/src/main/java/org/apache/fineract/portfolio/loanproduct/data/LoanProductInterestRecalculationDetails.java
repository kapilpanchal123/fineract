package org.apache.fineract.portfolio.loanproduct.data;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanProductInterestRecalculationDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private LoanProductDTOData loanProduct;
    private Integer interestRecalculationCompoundingMethod;
    private Integer rescheduleStrategyMethod;
    private Integer restFrequencyType;
    private Integer restInterval;
    private Integer restFrequencyNthDay;
    private Integer restFrequencyWeekday;
    private Integer restFrequencyOnDay;
    private Integer compoundingFrequencyType;
    private Integer compoundingInterval;
    private Integer compoundingFrequencyNthDay;
    private Integer compoundingFrequencyWeekday;
    private Integer compoundingFrequencyOnDay;
    private boolean isArrearsBasedOnOriginalSchedule;
    private Integer preCloseInterestCalculationStrategy;
    private Boolean isCompoundingToBePostedAsTransaction;
    private Boolean allowCompoundingOnEod;
    private Boolean disallowInterestCalculationOnPastDue;
}
