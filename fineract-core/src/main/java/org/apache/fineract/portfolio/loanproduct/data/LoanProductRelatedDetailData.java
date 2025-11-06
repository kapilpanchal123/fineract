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

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.portfolio.common.domain.DaysInYearCustomStrategyType;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanProductRelatedDetailData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Embedded
    private MonetaryCurrency currency;

    private BigDecimal principal;
    private BigDecimal nominalInterestRatePerPeriod;

    @Enumerated(EnumType.ORDINAL)
    private PeriodFrequencyType interestPeriodFrequencyType;

    private BigDecimal annualNominalInterestRate;

    @Enumerated(EnumType.ORDINAL)
    private InterestMethod interestMethod;

    @Enumerated(EnumType.ORDINAL)
    private InterestCalculationPeriodMethod interestCalculationPeriodMethod;

    private boolean allowPartialPeriodInterestCalculation;
    private Integer repayEvery;

    @Enumerated(EnumType.ORDINAL)
    private PeriodFrequencyType repaymentPeriodFrequencyType;

    private Integer fixedLength;
    private Integer numberOfRepayments;
    private Integer graceOnPrincipalPayment;
    private Integer recurringMoratoriumOnPrincipalPeriods;
    private Integer graceOnInterestPayment;
    private Integer graceOnInterestCharged;

    @Enumerated(EnumType.ORDINAL)
    private AmortizationMethod amortizationMethod;

    private BigDecimal inArrearsTolerance;
    private Integer graceOnArrearsAgeing;
    private Integer daysInMonthType;
    private Integer daysInYearType;
    private boolean isInterestRecalculationEnabled;
    private boolean isEqualAmortization = false;
    private boolean enableDownPayment;
    private BigDecimal disbursedAmountPercentageForDownPayment;
    private boolean enableAutoRepaymentForDownPayment;

    @Enumerated(EnumType.STRING)
    private LoanScheduleType loanScheduleType;

    @Enumerated(EnumType.STRING)
    private LoanScheduleProcessingType loanScheduleProcessingType;

    private boolean enableAccrualActivityPosting = false;

    // @Convert(converter = SupportedInterestRefundTypesListConverter.class)
    private List<LoanSupportedInterestRefundTypes> supportedInterestRefundTypes = List.of();

    @Enumerated(EnumType.STRING)
    private LoanChargeOffBehaviour chargeOffBehaviour;

    private boolean interestRecognitionOnDisbursementDate = false;

    @Enumerated(EnumType.STRING)
    private DaysInYearCustomStrategyType daysInYearCustomStrategy;

    private boolean enableIncomeCapitalization = false;

    @Enumerated(EnumType.STRING)
    private LoanCapitalizedIncomeCalculationType capitalizedIncomeCalculationType;

    @Enumerated(EnumType.STRING)
    private LoanCapitalizedIncomeStrategy capitalizedIncomeStrategy;

    @Enumerated(EnumType.STRING)
    private LoanCapitalizedIncomeType capitalizedIncomeType;

    private boolean enableBuyDownFee = false;

    @Enumerated(EnumType.STRING)
    private LoanBuyDownFeeCalculationType buyDownFeeCalculationType;

    @Enumerated(EnumType.STRING)
    private LoanBuyDownFeeStrategy buyDownFeeStrategy;

    @Enumerated(EnumType.STRING)
    private LoanBuyDownFeeIncomeType buyDownFeeIncomeType;

    private boolean merchantBuyDownFee = true;
    private Integer installmentAmountInMultiplesOf;
}
