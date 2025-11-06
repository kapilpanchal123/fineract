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
import java.util.List;
import java.util.UUID;
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
public class LoanProductRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String shortName;
    private String description;
    private UUID externalId;
    private Long fundId;
    private LocalDate startDate;
    private LocalDate closeDate;
    private boolean includeInBorrowerCycle;
    private String currencyCode;
    private Integer digitsAfterDecimal;
    private Integer inMultiplesOf;
    private Long installmentAmountInMultiplesOf;
    private boolean useBorrowerCycle;
    private BigDecimal minPrincipal;
    private BigDecimal principal;
    private BigDecimal maxPrincipal;
    private Integer minNumberOfRepayments;
    private Integer numberOfRepayments;
    private Integer maxNumberOfRepayments;
    private boolean isLinkedToFloatingInterestRates;
    private boolean allowApprovedDisbursedAmountsOverApplied;
    private String overAppliedCalculationType;
    private Integer overAppliedNumber;
    private Double minInterestRatePerPeriod;
    private Double interestRatePerPeriod;
    private Double maxInterestRatePerPeriod;
    private Integer interestRateFrequencyType;
    private Integer repaymentEvery;
    private Integer repaymentFrequencyType;
    private Integer repaymentStartDateType;
    private Integer fixedLength;
    private boolean interestRecognitionOnDisbursementDate;
    private List<LoanProductBorrowerCycleVariationData1> principalVariationsForBorrowerCycle;
    private List<LoanProductBorrowerCycleVariationData1> numberOfRepaymentVariationsForBorrowerCycle;
    private List<LoanProductBorrowerCycleVariationData1> interestRateVariationsForBorrowerCycle;
    private Integer amortizationType;
    private Integer interestType;
    private boolean isEqualAmortization;
    private Integer interestCalculationPeriodType;
    private String transactionProcessingStrategyCode;
    private Integer graceOnPrincipalPayment;
    private Integer graceOnInterestPayment;
    private Integer daysInYearType;
    private Integer daysInMonthType;
    private boolean canDefineInstallmentAmount;
    private boolean accountMovesOutOfNPAOnlyOnArrearsCompletion;
    private boolean allowVariableInstallments;
    private boolean disallowExpectedDisbursements;
    private boolean canUseForTopup;
    private boolean isInterestRecalculationEnabled;
    private boolean holdGuaranteeFunds;
    private LoanProductConfigurableAttributesData allowAttributeOverrides;
    private Integer delinquencyBucketId;
    private boolean enableDownPayment;
    private boolean enableInstallmentLevelDelinquency;
    private Integer dueDaysForRepaymentEvent;
    private Integer overDueDaysForRepaymentEvent;
    private LoanScheduleType loanScheduleType;
    private Integer disbursedAmountPercentageForDownPayment;
    private boolean enableAutoRepaymentForDownPayment;
    private List<ChargeData> charges;
    private Integer accountingRule;
    private String dateFormat;
    private String locale;
}
