package org.apache.fineract.portfolio.loanproduct.data;

import jakarta.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanProductTrancheDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private boolean multiDisburseLoan;
    private Integer maxTrancheCount;
    private BigDecimal outstandingLoanBalance;
}
