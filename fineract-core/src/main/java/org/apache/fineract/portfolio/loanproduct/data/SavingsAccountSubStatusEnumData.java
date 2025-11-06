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
public class SavingsAccountSubStatusEnumData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String code;
    private String value;
    private boolean none;
    private boolean inactive;
    private boolean dormant;
    private boolean escheat;
    private boolean block;
    private boolean blockCredit;
    private boolean blockDebit;
}
