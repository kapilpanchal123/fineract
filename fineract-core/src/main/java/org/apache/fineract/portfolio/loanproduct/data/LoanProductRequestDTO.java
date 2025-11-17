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
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.data.StringEnumOptionData;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanProductRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String shortName;
    private String description;
    private Long fundId;
    private String fundName;
    private String currencyCode;
    private Boolean includeInBorrowerCycle;
    private Boolean useBorrowerCycle;
    private LocalDate startDate;
    private LocalDate closeDate;
    private String status;
    private String externalId;
    // terms
    private CurrencyDataDTO currency;
    private BigDecimal principal;
    private BigDecimal minPrincipal;
    private BigDecimal maxPrincipal;
    private Integer numberOfRepayments;
    private Integer minNumberOfRepayments;
    private Integer maxNumberOfRepayments;
    private Integer repaymentEvery;
    private EnumOptionData repaymentFrequencyType;
    private Integer fixedLength;
    private BigDecimal interestRatePerPeriod;
    private BigDecimal minInterestRatePerPeriod;
    private BigDecimal maxInterestRatePerPeriod;
    private EnumOptionData interestRateFrequencyType;
    private BigDecimal annualInterestRate;

    private Boolean isLinkedToFloatingInterestRates;
    private Integer floatingRateId;
    private String floatingRateName;
    private BigDecimal interestRateDifferential;
    private BigDecimal minDifferentialLendingRate;
    private BigDecimal defaultDifferentialLendingRate;
    private BigDecimal maxDifferentialLendingRate;
    private Boolean isFloatingInterestRateCalculationAllowed;

    // Variable Installments Settings
    private Boolean allowVariableInstallments;
    private Integer minimumGap;
    private Integer maximumGap;

    // settings
    private EnumOptionData amortizationType;
    private EnumOptionData interestType;
    private EnumOptionData interestCalculationPeriodType;
    private Boolean allowPartialPeriodInterestCalculation;
    private BigDecimal inArrearsTolerance;
    private String transactionProcessingStrategyCode;
    private String transactionProcessingStrategyName;
    private List<AdvancedPaymentDataDTO> paymentAllocation;
    private List<CreditAllocationDataDTO> creditAllocation;
    private Integer graceOnPrincipalPayment;
    private Integer recurringMoratoriumOnPrincipalPeriods;
    private Integer graceOnInterestPayment;
    private Integer graceOnInterestCharged;
    private Integer graceOnArrearsAgeing;
    private Integer overdueDaysForNPA;
    private EnumOptionData daysInMonthType;
    private EnumOptionData daysInYearType;
    private Boolean isInterestRecalculationEnabled;
    private LoanProductInterestRecalculationDataDTO interestRecalculationData;
    private Integer minimumDaysBetweenDisbursalAndFirstRepayment;
    private Boolean canDefineInstallmentAmount;
    private Integer installmentAmountInMultiplesOf;
    private EnumOptionData repaymentStartDateType;
    private List<StringEnumOptionData> supportedInterestRefundTypes;
    private StringEnumOptionData chargeOffBehaviour;

    // charges
    private List<ChargeDataDTO> charges;

    private List<LoanProductBorrowerCycleVariationDataDTO> principalVariationsForBorrowerCycle;
    private List<LoanProductBorrowerCycleVariationDataDTO> interestRateVariationsForBorrowerCycle;
    private List<LoanProductBorrowerCycleVariationDataDTO> numberOfRepaymentVariationsForBorrowerCycle;
    // accounting
    private EnumOptionData accountingRule;
    private Boolean canUseForTopup;
    private Map<String, Object> accountingMappings;
    private List<PaymentTypeToGLAccountMapperDTO> paymentChannelToFundSourceMappings;
    private List<ChargeToGLAccountMapperDTO> feeToIncomeAccountMappings;
    private List<ChargeToGLAccountMapperDTO> penaltyToIncomeAccountMappings;
    private List<AdvancedMappingToExpenseAccountDataDTO> chargeOffReasonToExpenseAccountMappings;
    private Boolean enableAccrualActivityPosting;
    private List<AdvancedMappingToExpenseAccountDataDTO> writeOffReasonsToExpenseMappings;
    private List<CodeValueDataDTO> writeOffReasonOptions;
    // rates
    private Boolean isRatesEnabled;
    private List<RateDataDTO> rates;

    // template related
    private List<FundDataDTO> fundOptions;
    private List<PaymentTypeDataDTO> paymentTypeOptions;
    private List<CurrencyDataDTO> currencyOptions;
    private List<EnumOptionData> repaymentFrequencyTypeOptions;
    private List<EnumOptionData> interestRateFrequencyTypeOptions;
    private List<EnumOptionData> amortizationTypeOptions;
    private List<EnumOptionData> interestTypeOptions;
    private List<EnumOptionData> interestCalculationPeriodTypeOptions;
    private List<TransactionProcessingStrategyDataDTO> transactionProcessingStrategyOptions;
    private List<ChargeDataDTO> chargeOptions;
    private List<RateDataDTO> rateOptions;
    private List<ChargeDataDTO> penaltyOptions;
    private List<EnumOptionData> accountingRuleOptions;
    private Map<String, List<GLAccountDataDTO>> accountingMappingOptions;
    private List<EnumOptionData> valueConditionTypeOptions;
    private List<EnumOptionData> daysInMonthTypeOptions;
    private List<EnumOptionData> daysInYearTypeOptions;
    private List<EnumOptionData> interestRecalculationCompoundingTypeOptions;
    private List<EnumOptionData> interestRecalculationNthDayTypeOptions;
    private List<EnumOptionData> interestRecalculationDayOfWeekTypeOptions;
    private List<EnumOptionData> rescheduleStrategyTypeOptions;
    private List<EnumOptionData> preClosureInterestCalculationStrategyOptions;
    private List<EnumOptionData> advancedPaymentAllocationTransactionTypes;
    private List<EnumOptionData> advancedPaymentAllocationFutureInstallmentAllocationRules;
    private List<EnumOptionData> advancedPaymentAllocationTypes;

    private List<EnumOptionData> creditAllocationTransactionTypes;
    private List<EnumOptionData> creditAllocationAllocationTypes;

    private List<EnumOptionData> loanScheduleTypeOptions;
    private List<EnumOptionData> loanScheduleProcessingTypeOptions;

    private List<EnumOptionData> interestRecalculationFrequencyTypeOptions;
    private List<FloatingRateDataDTO> floatingRateOptions;
    private List<EnumOptionData> repaymentStartDateTypeOptions;
    private List<StringEnumOptionData> supportedInterestRefundTypesOptions;
    private List<StringEnumOptionData> chargeOffBehaviourOptions;

    private Boolean multiDisburseLoan;
    private Integer maxTrancheCount;
    private BigDecimal outstandingLoanBalance;
    private Boolean disallowExpectedDisbursements;
    private Boolean allowApprovedDisbursedAmountsOverApplied;
    private String overAppliedCalculationType;
    private Integer overAppliedNumber;

    private BigDecimal principalThresholdForLastInstallment;

    private Boolean holdGuaranteeFunds;
    private LoanProductGuaranteeDataDTO productGuaranteeData;
    private Boolean accountMovesOutOfNPAOnlyOnArrearsCompletion;
    private LoanProductConfigurableAttributesDTO allowAttributeOverrides;
    private Boolean syncExpectedWithDisbursementDate;
    private Boolean isEqualAmortization;
    private BigDecimal fixedPrincipalPercentagePerInstallment;

    private List<CodeValueDataDTO> chargeOffReasonOptions;

    // Delinquency Buckets
    private List<DelinquencyBucketDataDTO> delinquencyBucketOptions;
    private DelinquencyBucketDataDTO delinquencyBucket;

    private Integer dueDaysForRepaymentEvent;
    private Integer overDueDaysForRepaymentEvent;

    private Boolean enableDownPayment;
    private BigDecimal disbursedAmountPercentageForDownPayment;
    private Boolean enableAutoRepaymentForDownPayment;
    private Boolean enableInstallmentLevelDelinquency;

    private EnumOptionData loanScheduleType;
    private EnumOptionData loanScheduleProcessingType;
    private Boolean interestRecognitionOnDisbursementDate;
    private List<StringEnumOptionData> daysInYearCustomStrategyOptions;
    private StringEnumOptionData daysInYearCustomStrategy;
    private Boolean enableIncomeCapitalization;
    private StringEnumOptionData capitalizedIncomeCalculationType;
    private StringEnumOptionData capitalizedIncomeStrategy;
    private StringEnumOptionData capitalizedIncomeType;
    private List<StringEnumOptionData> capitalizedIncomeCalculationTypeOptions;
    private List<StringEnumOptionData> capitalizedIncomeStrategyOptions;
    private List<StringEnumOptionData> capitalizedIncomeTypeOptions;
    private Boolean enableBuyDownFee;
    private StringEnumOptionData buyDownFeeCalculationType;
    private StringEnumOptionData buyDownFeeStrategy;
    private StringEnumOptionData buyDownFeeIncomeType;
    private Boolean merchantBuyDownFee;
    private List<StringEnumOptionData> buyDownFeeCalculationTypeOptions;
    private List<StringEnumOptionData> buyDownFeeStrategyOptions;
    private List<StringEnumOptionData> buyDownFeeIncomeTypeOptions;

    private List<CodeValueDataDTO> capitalizedIncomeClassificationOptions;
    private List<CodeValueDataDTO> buydownFeeClassificationOptions;
    private List<ClassificationToGLAccountDataDTO> capitalizedIncomeClassificationToIncomeAccountMappings;
    private List<ClassificationToGLAccountDataDTO> buydownFeeClassificationToIncomeAccountMappings;
}
