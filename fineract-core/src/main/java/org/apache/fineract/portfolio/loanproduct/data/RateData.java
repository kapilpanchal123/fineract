package org.apache.fineract.portfolio.loanproduct.data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
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
public class RateData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private BigDecimal percentage;
    private Integer productApply;
    private boolean active;
    private AppUserData approveUser;
}
