/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.loanproduct.data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingsAccountChargeData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long chargeId;
    private Long accountId;
    private String name;
    private EnumOptionData chargeTimeType;
    private LocalDate dueDate;
    private MonthDay feeOnMonthDay;
    private Integer feeInterval;
    private EnumOptionData chargeCalculationType;
    private BigDecimal percentage;
    private BigDecimal amountPercentageAppliedTo;
    private CurrencyData currency;
    private BigDecimal amount;
    private BigDecimal amountPaid;
    private BigDecimal amountWaived;
    private BigDecimal amountWrittenOff;
    private BigDecimal amountOutstanding;
    private BigDecimal amountOrPercentage;
    private boolean penalty;
    private Boolean isActive;
    private Boolean isFreeWithdrawal;
    private Integer freeWithdrawalChargeFrequency;
    private Integer restartFrequency;
    private Integer restartFrequencyEnum;
    private LocalDate inactivationDate;
    private List<ChargeData> chargeOptions;
    private ChargeData chargeData;
}
