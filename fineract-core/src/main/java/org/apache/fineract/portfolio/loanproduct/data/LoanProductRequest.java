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
import java.util.Set;

import jakarta.persistence.Column;
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
    private String externalId;
    private Long fundId;
    private LocalDate startDate;
    private LocalDate closeDate;
    private Boolean includeInBorrowerCycle;
    private String currencyCode;
    private Integer digitsAfterDecimal;
    private Integer inMultiplesOf;
    private Integer installmentAmountInMultiplesOf;
    private Boolean useBorrowerCycle;
    private BigDecimal minPrincipal;
    private BigDecimal principal;
    private BigDecimal maxPrincipal;
    private BigDecimal minNominalInterestRatePerPeriod;
    private BigDecimal maxNominalInterestRatePerPeriod;
    private Integer minNumberOfRepayments;
    private Integer numberOfRepayments;
    private Integer maxNumberOfRepayments;
    private Boolean isLinkedToFloatingInterestRates;
    private Boolean allowApprovedDisbursedAmountsOverApplied;
    private String overAppliedCalculationType;
    private Integer overAppliedNumber;
    private BigDecimal minInterestRatePerPeriod;
    private BigDecimal interestRatePerPeriod;
    private BigDecimal maxInterestRatePerPeriod;
    private Integer interestRateFrequencyType;
    private Integer repaymentEvery;
    private Integer repaymentFrequencyType;
    private Integer repaymentStartDateType;
    private Integer fixedLength;
    private Boolean interestRecognitionOnDisbursementDate;
    private Set<LoanProductBorrowerCycleVariationsDTO> principalVariationsForBorrowerCycle;
    private Set<LoanProductBorrowerCycleVariationsDTO> numberOfRepaymentVariationsForBorrowerCycle;
    private Set<LoanProductBorrowerCycleVariationsDTO> interestRateVariationsForBorrowerCycle;
    private Set<LoanProductBorrowerCycleVariationsDTO> loanProductBorrowerCycleVariations;
    private Integer amortizationType;
    private Boolean allowPartialPeriodInterestCalculation;
    private Integer interestType;
    private Boolean isEqualAmortization;
    private Integer interestCalculationPeriod;
    private String transactionProcessingStrategyCode;
    private Integer graceOnPrincipalPayment;
    private Integer graceOnInterestPayment;
    private Integer graceOnInterestCharged;
    private Integer daysInYearType;
    private Integer daysInMonthType;
    private Boolean canDefineInstallmentAmount;
    private Boolean accountMovesOutOfNPAOnlyOnArrearsCompletion;
    private Boolean allowVariableInstallments;
    private Boolean disallowExpectedDisbursements;
    private Boolean canUseForTopup;
    private Boolean isInterestRecalculationEnabled;
    private String daysInYearCustomStrategy;
    private Boolean holdGuaranteeFunds;
//    private Boolean amortizationTypeAllowed;
//    private Boolean interestTypeAllowed;
//    private Boolean transactionProcessingStrategyCodeAllowed;
//    private Boolean interestCalculationPeriodTypeAllowed;
//    private Boolean inArrearsToleranceAllowed;
//    private Boolean repaymentEveryAllowed;
//    private Boolean graceOnPrincipalAndInterestPaymentAllowed;
//    private Boolean graceOnArrearsAgeingAllowed;
//    private BigDecimal inArrearsTolerance;
    private Long delinquencyBucketId;
    private Boolean enableDownPayment;
    private Boolean enableInstallmentLevelDelinquency;
    private Integer dueDaysForRepaymentEvent;
    private Integer overDueDaysForRepaymentEvent;
    private Integer overdueDaysForNPA;
    private String loanScheduleType;
    private BigDecimal disbursedAmountPercentageForDownPayment;
    private Boolean enableAutoRepaymentForDownPayment;
    private List<Long> charges;
    private List<Long> rates;
    private List<String> paymentAllocationRule;
    private String futureInstallmentAllocationRule;
    private String paymentAllocationTransactionType;
    private List<String> loanProductCreditAllocationRule;
    private String loanProductCreditAllocationTransactionType;
    private Long floatingRatesId;
    private Integer accountingRule;
    private BigDecimal interestRateDifferential;
    private BigDecimal minDifferentialLendingRate;
    private BigDecimal defaultDifferentialLendingRate;
    private BigDecimal maxDifferentialLendingRate;
    private Boolean isFloatingInterestRateCalculationAllowed;
    private Integer minimumGapBetweenInstallments;
    private Integer maximumGapBetweenInstallments;
    private Integer recurringMoratoriumOnPrincipalPeriods;
    private Integer minimumDaysBetweenDisbursalAndFirstRepayment;
    private String loanScheduleProcessingType;
    private Boolean multiDisburseLoan;
    private BigDecimal outstandingLoanBalance;
    private Integer maxTrancheCount;
    private Integer graceOnArrearsAgeing;
    private Integer interestRecalculationCompoundingMethod;
    private Integer rescheduleStrategyMethod;
    private Integer recalculationRestFrequencyType;
    private Integer recalculationRestFrequencyNthDayType;
    private Integer recalculationRestFrequencyOnDayType;
    private Integer recalculationRestFrequencyDayOfWeekType;
    private Integer recalculationRestFrequencyInterval;
    private Boolean isArrearsBasedOnOriginalSchedule;
    private Integer recalculationCompoundingFrequencyType;
    private Integer recalculationCompoundingFrequencyInterval;
    private Integer recalculationCompoundingFrequencyNthDayType;
    private Integer recalculationCompoundingFrequencyOnDayType;
    private Integer recalculationCompoundingFrequencyDayOfWeekType;
    private Boolean allowCompoundingOnEod;
    private Integer preClosureInterestCalculationStrategy;
    private Boolean isCompoundingToBePostedAsTransaction;
    private Boolean disallowInterestCalculationOnPastDue;
    private BigDecimal mandatoryGuarantee;
    private BigDecimal minimumGuaranteeFromGuarantor;
    private BigDecimal minimumGuaranteeFromOwnFunds;
    private LoanProductConfigurableAttributesDTO allowAttributeOverrides;
    private BigDecimal principalThresholdForLastInstallment;
    private Boolean syncExpectedWithDisbursementDate;
    private BigDecimal fixedPrincipalPercentagePerInstallment;
    private Boolean enableAccrualActivityPosting;
    private List<String> supportedInterestRefundTypes;
    private String chargeOffBehaviour;
    private Boolean enableIncomeCapitalization;
    private String capitalizedIncomeCalculationType;
    private String capitalizedIncomeStrategy;
    private String capitalizedIncomeType;
    private Boolean enableBuyDownFee;
    private String buyDownFeeCalculationType;
    private String buyDownFeeStrategy;
    private String buyDownFeeIncomeType;
    private Boolean merchantBuyDownFee;
    private BigDecimal inArrearsTolerance;
}
