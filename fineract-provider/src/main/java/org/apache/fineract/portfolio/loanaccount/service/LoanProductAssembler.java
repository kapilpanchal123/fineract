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
package org.apache.fineract.portfolio.loanaccount.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.accounting.common.AccountingRuleType;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.StringEnumOptionData;
import org.apache.fineract.infrastructure.core.domain.ExternalId;
import org.apache.fineract.infrastructure.core.service.ExternalIdFactory;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.common.domain.DaysInMonthType;
import org.apache.fineract.portfolio.common.domain.DaysInYearCustomStrategyType;
import org.apache.fineract.portfolio.common.domain.DaysInYearType;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.floatingrates.domain.FloatingRate;
import org.apache.fineract.portfolio.fund.domain.Fund;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanBuyDownFeeCalculationType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanBuyDownFeeIncomeType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanBuyDownFeeStrategy;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCapitalizedIncomeCalculationType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCapitalizedIncomeStrategy;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCapitalizedIncomeType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanChargeOffBehaviour;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.AprCalculator;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleProcessingType;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleType;
import org.apache.fineract.portfolio.loanproduct.LoanProductConstants;
import org.apache.fineract.portfolio.loanproduct.data.AccountingRuleTypeENUM;
import org.apache.fineract.portfolio.loanproduct.data.AmortizationMethodENUM;
import org.apache.fineract.portfolio.loanproduct.data.DaysInMonthTypeENUM;
import org.apache.fineract.portfolio.loanproduct.data.DaysInYearCustomStrategyTypeENUM;
import org.apache.fineract.portfolio.loanproduct.data.DaysInYearTypeENUM;
import org.apache.fineract.portfolio.loanproduct.data.ExternalIdDTO;
import org.apache.fineract.portfolio.loanproduct.data.InterestCalculationPeriodMethodENUM;
import org.apache.fineract.portfolio.loanproduct.data.InterestMethodENUM;
import org.apache.fineract.portfolio.loanproduct.data.LoanBuyDownFeeCalculationTypeENUM;
import org.apache.fineract.portfolio.loanproduct.data.LoanBuyDownFeeIncomeTypeENUM;
import org.apache.fineract.portfolio.loanproduct.data.LoanBuyDownFeeStrategyENUM;
import org.apache.fineract.portfolio.loanproduct.data.LoanCapitalizedIncomeCalculationTypeENUM;
import org.apache.fineract.portfolio.loanproduct.data.LoanCapitalizedIncomeStrategyENUM;
import org.apache.fineract.portfolio.loanproduct.data.LoanCapitalizedIncomeTypeENUM;
import org.apache.fineract.portfolio.loanproduct.data.LoanChargeOffBehaviourENUM;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductBorrowerCycleVariationDataDTO;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductBorrowerCycleVariationsDTO;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductConfigurableAttributesDTO;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductDTO;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductGuaranteeDetailsDTO;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductInterestRecalculationDetailsDTO;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductMinMaxConstraintsDTO;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductRelatedDetailDTO;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductRequestDTO;
import org.apache.fineract.portfolio.loanproduct.data.LoanScheduleProcessingTypeENUM;
import org.apache.fineract.portfolio.loanproduct.data.LoanScheduleTypeENUM;
import org.apache.fineract.portfolio.loanproduct.data.LoanSupportedInterestRefundTypesENUM;
import org.apache.fineract.portfolio.loanproduct.data.MonetaryCurrencyDTO;
import org.apache.fineract.portfolio.loanproduct.data.PeriodFrequencyTypeENUM;
import org.apache.fineract.portfolio.loanproduct.data.RepaymentStartDateTypeENUM;
import org.apache.fineract.portfolio.loanproduct.domain.AmortizationMethod;
import org.apache.fineract.portfolio.loanproduct.domain.InterestCalculationPeriodMethod;
import org.apache.fineract.portfolio.loanproduct.domain.InterestMethod;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductBorrowerCycleVariations;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductConfigurableAttributes;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductCreditAllocationRule;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductGuaranteeDetails;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductInterestRecalculationDetails;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductParamType;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductPaymentAllocationRule;
import org.apache.fineract.portfolio.loanproduct.domain.LoanSupportedInterestRefundTypes;
import org.apache.fineract.portfolio.loanproduct.domain.RepaymentStartDateType;
import org.apache.fineract.portfolio.rate.domain.Rate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanProductAssembler {

    private final LoanProductInterestRecalculationDetailsAssembler interestRecalculationDetailsAssembler;
    private final LoanProductGuaranteeDetailsAssembler guaranteeDetailsAssembler;
    private final AprCalculator aprCalculator;

    public LoanProduct assembleFromJson(final Fund fund, final String loanTransactionProcessingStrategy, final List<Charge> productCharges,
            final JsonCommand command, final AprCalculator aprCalculator, FloatingRate floatingRate, final List<Rate> productRates,
            List<LoanProductPaymentAllocationRule> loanProductPaymentAllocationRules,
            List<LoanProductCreditAllocationRule> loanProductCreditAllocationRules) {

        final String name = command.stringValueOfParameterNamed("name");
        final String shortName = command.stringValueOfParameterNamed(LoanProductConstants.SHORT_NAME);
        final String description = command.stringValueOfParameterNamed("description");
        final String currencyCode = command.stringValueOfParameterNamed("currencyCode");
        final Integer digitsAfterDecimal = command.integerValueOfParameterNamed("digitsAfterDecimal");
        final Integer inMultiplesOf = command.integerValueOfParameterNamed("inMultiplesOf");

        final MonetaryCurrency currency = new MonetaryCurrency(currencyCode, digitsAfterDecimal, inMultiplesOf);
        final BigDecimal principal = command.bigDecimalValueOfParameterNamed("principal");
        final BigDecimal minPrincipal = command.bigDecimalValueOfParameterNamed("minPrincipal");
        final BigDecimal maxPrincipal = command.bigDecimalValueOfParameterNamed("maxPrincipal");

        final InterestMethod interestMethod = InterestMethod.fromInt(command.integerValueOfParameterNamed("interestType"));
        final InterestCalculationPeriodMethod interestCalculationPeriodMethod = InterestCalculationPeriodMethod
                .fromInt(command.integerValueOfParameterNamed("interestCalculationPeriodType"));
        final boolean allowPartialPeriodInterestCalcualtion = command
                .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.ALLOW_PARTIAL_PERIOD_INTEREST_CALCUALTION_PARAM_NAME);
        final AmortizationMethod amortizationMethod = AmortizationMethod.fromInt(command.integerValueOfParameterNamed("amortizationType"));
        final PeriodFrequencyType repaymentFrequencyType = PeriodFrequencyType
                .fromInt(command.integerValueOfParameterNamed("repaymentFrequencyType"));
        PeriodFrequencyType interestFrequencyType = PeriodFrequencyType.INVALID;
        BigDecimal interestRatePerPeriod = null;
        BigDecimal minInterestRatePerPeriod = null;
        BigDecimal maxInterestRatePerPeriod = null;
        BigDecimal annualInterestRate = null;
        BigDecimal interestRateDifferential = null;
        BigDecimal minDifferentialLendingRate = null;
        BigDecimal maxDifferentialLendingRate = null;
        BigDecimal defaultDifferentialLendingRate = null;
        Boolean isFloatingInterestRateCalculationAllowed = null;

        Integer minimumGapBetweenInstallments = null;
        Integer maximumGapBetweenInstallments = null;

        // Declaring this variable here to be used throughout the file
        final DaysInYearType daysInYearType = DaysInYearType
                .fromInt(command.integerValueOfParameterNamed(LoanProductConstants.DAYS_IN_YEAR_TYPE_PARAMETER_NAME));

        final Integer repaymentEvery = command.integerValueOfParameterNamed("repaymentEvery");
        final Integer numberOfRepayments = command.integerValueOfParameterNamed("numberOfRepayments");
        final Boolean isLinkedToFloatingInterestRates = command.booleanObjectValueOfParameterNamed("isLinkedToFloatingInterestRates");
        if (isLinkedToFloatingInterestRates != null && isLinkedToFloatingInterestRates) {
            interestRateDifferential = command.bigDecimalValueOfParameterNamed("interestRateDifferential");
            minDifferentialLendingRate = command.bigDecimalValueOfParameterNamed("minDifferentialLendingRate");
            maxDifferentialLendingRate = command.bigDecimalValueOfParameterNamed("maxDifferentialLendingRate");
            defaultDifferentialLendingRate = command.bigDecimalValueOfParameterNamed("defaultDifferentialLendingRate");
            isFloatingInterestRateCalculationAllowed = command
                    .booleanObjectValueOfParameterNamed("isFloatingInterestRateCalculationAllowed");
        } else {
            interestFrequencyType = PeriodFrequencyType.fromInt(command.integerValueOfParameterNamed("interestRateFrequencyType"));
            interestRatePerPeriod = command.bigDecimalValueOfParameterNamed("interestRatePerPeriod");
            minInterestRatePerPeriod = command.bigDecimalValueOfParameterNamed("minInterestRatePerPeriod");
            maxInterestRatePerPeriod = command.bigDecimalValueOfParameterNamed("maxInterestRatePerPeriod");
            annualInterestRate = aprCalculator.calculateFrom(interestFrequencyType, interestRatePerPeriod, numberOfRepayments,
                    repaymentEvery, repaymentFrequencyType, daysInYearType);

        }

        final Boolean isVariableInstallmentsAllowed = command
                .booleanObjectValueOfParameterNamed(LoanProductConstants.allowVariableInstallmentsParamName);
        if (isVariableInstallmentsAllowed != null && isVariableInstallmentsAllowed) {
            minimumGapBetweenInstallments = command.integerValueOfParameterNamed(LoanProductConstants.minimumGapBetweenInstallments);
            maximumGapBetweenInstallments = command.integerValueOfParameterNamed(LoanProductConstants.maximumGapBetweenInstallments);
        }

        final Integer minNumberOfRepayments = command.integerValueOfParameterNamed("minNumberOfRepayments");
        final Integer maxNumberOfRepayments = command.integerValueOfParameterNamed("maxNumberOfRepayments");
        final BigDecimal inArrearsTolerance = command.bigDecimalValueOfParameterNamed("inArrearsTolerance");

        // grace details
        final Integer graceOnPrincipalPayment = command.integerValueOfParameterNamed("graceOnPrincipalPayment");
        final Integer recurringMoratoriumOnPrincipalPeriods = command.integerValueOfParameterNamed("recurringMoratoriumOnPrincipalPeriods");
        final Integer graceOnInterestPayment = command.integerValueOfParameterNamed("graceOnInterestPayment");
        final Integer graceOnInterestCharged = command.integerValueOfParameterNamed("graceOnInterestCharged");
        final Integer minimumDaysBetweenDisbursalAndFirstRepayment = command
                .integerValueOfParameterNamed(LoanProductConstants.MINIMUM_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT);

        final AccountingRuleType accountingRuleType = AccountingRuleType.fromInt(command.integerValueOfParameterNamed("accountingRule"));
        final boolean includeInBorrowerCycle = command.booleanPrimitiveValueOfParameterNamed("includeInBorrowerCycle");

        final LocalDate startDate = command.localDateValueOfParameterNamed("startDate");
        final LocalDate closeDate = command.localDateValueOfParameterNamed("closeDate");
        final ExternalId externalId = ExternalIdFactory.produce(command.stringValueOfParameterNamedAllowingNull("externalId"));

        final LoanScheduleType loanScheduleType;
        if (command.hasParameter("loanScheduleType")) {
            loanScheduleType = LoanScheduleType.valueOf(command.stringValueOfParameterNamed("loanScheduleType"));
        } else {
            // For backward compatibility
            loanScheduleType = LoanScheduleType.CUMULATIVE;
        }

        final LoanScheduleProcessingType loanScheduleProcessingType;
        if (LoanScheduleType.PROGRESSIVE.equals(loanScheduleType) && command.hasParameter("loanScheduleProcessingType")) {
            loanScheduleProcessingType = LoanScheduleProcessingType
                    .valueOf(command.stringValueOfParameterNamed("loanScheduleProcessingType"));
        } else {
            // For backward compatibility
            loanScheduleProcessingType = LoanScheduleProcessingType.HORIZONTAL;
        }

        final boolean useBorrowerCycle = command
                .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.USE_BORROWER_CYCLE_PARAMETER_NAME);
        final Set<LoanProductBorrowerCycleVariations> loanProductBorrowerCycleVariations = new HashSet<>();

        if (useBorrowerCycle) {
            populateBorrowerCycleVariations(command, loanProductBorrowerCycleVariations);
        }

        final boolean multiDisburseLoan = command
                .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.MULTI_DISBURSE_LOAN_PARAMETER_NAME);
        Integer maxTrancheCount = null;
        BigDecimal outstandingLoanBalance = null;
        if (multiDisburseLoan) {
            outstandingLoanBalance = command.bigDecimalValueOfParameterNamed(LoanProductConstants.OUTSTANDING_LOAN_BALANCE_PARAMETER_NAME);
            maxTrancheCount = command.integerValueOfParameterNamed(LoanProductConstants.MAX_TRANCHE_COUNT_PARAMETER_NAME);
        }

        final Integer graceOnArrearsAgeing = command
                .integerValueOfParameterNamed(LoanProductConstants.GRACE_ON_ARREARS_AGEING_PARAMETER_NAME);

        final Integer overdueDaysForNPA = command.integerValueOfParameterNamed(LoanProductConstants.OVERDUE_DAYS_FOR_NPA_PARAMETER_NAME);

        // Interest recalculation settings
        final boolean isInterestRecalculationEnabled = command
                .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.IS_INTEREST_RECALCULATION_ENABLED_PARAMETER_NAME);
        final DaysInMonthType daysInMonthType = DaysInMonthType
                .fromInt(command.integerValueOfParameterNamed(LoanProductConstants.DAYS_IN_MONTH_TYPE_PARAMETER_NAME));

        final DaysInYearCustomStrategyType daysInYearCustomStrategy = command.enumValueOfParameterNamed(
                LoanProductConstants.DAYS_IN_YEAR_CUSTOM_STRATEGY_TYPE_PARAMETER_NAME, DaysInYearCustomStrategyType.class);

        LoanProductInterestRecalculationDetails interestRecalculationSettings = null;

        if (isInterestRecalculationEnabled) {
            interestRecalculationSettings = interestRecalculationDetailsAssembler.createFrom(command);
        }

        final boolean holdGuarantorFunds = command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.holdGuaranteeFundsParamName);
        LoanProductGuaranteeDetails loanProductGuaranteeDetails = null;
        if (holdGuarantorFunds) {
            loanProductGuaranteeDetails = guaranteeDetailsAssembler.createFrom(command);
        }

        LoanProductConfigurableAttributes loanConfigurableAttributes = null;
        if (command.parameterExists(LoanProductConstants.allowAttributeOverridesParamName)) {
            loanConfigurableAttributes = LoanProductConfigurableAttributes.createFrom(command);
        } else {
            loanConfigurableAttributes = LoanProductConfigurableAttributes.populateDefaultsForConfigurableAttributes();
        }

        BigDecimal principalThresholdForLastInstallment = command
                .bigDecimalValueOfParameterNamed(LoanProductConstants.principalThresholdForLastInstallmentParamName);

        if (principalThresholdForLastInstallment == null) {
            principalThresholdForLastInstallment = multiDisburseLoan
                    ? LoanProductConstants.DEFAULT_PRINCIPAL_THRESHOLD_FOR_MULTI_DISBURSE_LOAN
                    : LoanProductConstants.DEFAULT_PRINCIPAL_THRESHOLD_FOR_SINGLE_DISBURSE_LOAN;
        }
        final boolean accountMovesOutOfNPAOnlyOnArrearsCompletion = command
                .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.ACCOUNT_MOVES_OUT_OF_NPA_ONLY_ON_ARREARS_COMPLETION_PARAM_NAME);
        final boolean canDefineEmiAmount = command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.canDefineEmiAmountParamName);
        final Integer installmentAmountInMultiplesOf = command
                .integerValueOfParameterNamed(LoanProductConstants.installmentAmountInMultiplesOfParamName);

        final boolean syncExpectedWithDisbursementDate = command.booleanPrimitiveValueOfParameterNamed("syncExpectedWithDisbursementDate");

        final boolean canUseForTopup = command.parameterExists(LoanProductConstants.CAN_USE_FOR_TOPUP)
                && command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.CAN_USE_FOR_TOPUP);

        final boolean isEqualAmortization = command.parameterExists(LoanProductConstants.IS_EQUAL_AMORTIZATION_PARAM)
                && command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.IS_EQUAL_AMORTIZATION_PARAM);

        BigDecimal fixedPrincipalPercentagePerInstallment = command
                .bigDecimalValueOfParameterNamed(LoanProductConstants.fixedPrincipalPercentagePerInstallmentParamName);

        final boolean disallowExpectedDisbursements = command.parameterExists(LoanProductConstants.DISALLOW_EXPECTED_DISBURSEMENTS)
                && command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.DISALLOW_EXPECTED_DISBURSEMENTS);

        final boolean allowApprovedDisbursedAmountsOverApplied = command
                .parameterExists(LoanProductConstants.ALLOW_APPROVED_DISBURSED_AMOUNTS_OVER_APPLIED)
                && command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.ALLOW_APPROVED_DISBURSED_AMOUNTS_OVER_APPLIED);

        final String overAppliedCalculationType = command
                .stringValueOfParameterNamedAllowingNull(LoanProductConstants.OVER_APPLIED_CALCULATION_TYPE);

        final Integer overAppliedNumber = command.integerValueOfParameterNamed(LoanProductConstants.OVER_APPLIED_NUMBER);

        final Integer dueDaysForRepaymentEvent = command.integerValueOfParameterNamed(LoanProductConstants.DUE_DAYS_FOR_REPAYMENT_EVENT);
        final Integer overDueDaysForRepaymentEvent = command
                .integerValueOfParameterNamed(LoanProductConstants.OVER_DUE_DAYS_FOR_REPAYMENT_EVENT);

        final boolean enableDownPayment = command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.ENABLE_DOWN_PAYMENT);
        final BigDecimal disbursedAmountPercentageDownPayment = command
                .bigDecimalValueOfParameterNamed(LoanProductConstants.DISBURSED_AMOUNT_PERCENTAGE_DOWN_PAYMENT);
        final boolean enableAutoRepaymentForDownPayment = command
                .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.ENABLE_AUTO_REPAYMENT_DOWN_PAYMENT);

        final RepaymentStartDateType repaymentStartDateType = RepaymentStartDateType
                .fromInt(command.integerValueOfParameterNamed(LoanProductConstants.REPAYMENT_START_DATE_TYPE));

        final boolean enableInstallmentLevelDelinquency = command
                .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.ENABLE_INSTALLMENT_LEVEL_DELINQUENCY);

        final Integer fixedLength = command.integerValueOfParameterNamed(LoanProductConstants.FIXED_LENGTH);

        final boolean enableAccrualActivityPosting = command
                .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.ENABLE_ACCRUAL_ACTIVITY_POSTING);

        boolean interestRecognitionOnDisbursementDate = false;
        if (command.parameterExists(LoanProductConstants.INTEREST_RECOGNITION_ON_DISBURSEMENT_DATE)) {
            interestRecognitionOnDisbursementDate = command
                    .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.INTEREST_RECOGNITION_ON_DISBURSEMENT_DATE);
        }

        List<LoanSupportedInterestRefundTypes> supportedInterestRefundTypes = new ArrayList<>();
        if (command.parameterExists(LoanProductConstants.SUPPORTED_INTEREST_REFUND_TYPES)) {
            JsonArray supportedTransactionsForInterestRefund = command
                    .arrayOfParameterNamed(LoanProductConstants.SUPPORTED_INTEREST_REFUND_TYPES);
            supportedTransactionsForInterestRefund.iterator().forEachRemaining(value -> {
                supportedInterestRefundTypes.add(LoanSupportedInterestRefundTypes.valueOf(value.getAsString()));
            });
        }

        final LoanChargeOffBehaviour chargeOffBehaviour;
        if (command.parameterExists(LoanProductConstants.CHARGE_OFF_BEHAVIOUR)) {
            chargeOffBehaviour = LoanChargeOffBehaviour
                    .valueOf(command.stringValueOfParameterNamed(LoanProductConstants.CHARGE_OFF_BEHAVIOUR));
        } else {
            // For backward compatibility
            chargeOffBehaviour = LoanChargeOffBehaviour.REGULAR;
        }

        final boolean enableIncomeCapitalization = command
                .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.ENABLE_INCOME_CAPITALIZATION_PARAM_NAME);
        final LoanCapitalizedIncomeCalculationType capitalizedIncomeCalculationType = command.enumValueOfParameterNamed(
                LoanProductConstants.CAPITALIZED_INCOME_CALCULATION_TYPE_PARAM_NAME, LoanCapitalizedIncomeCalculationType.class);
        final LoanCapitalizedIncomeStrategy capitalizedIncomeStrategy = command.enumValueOfParameterNamed(
                LoanProductConstants.CAPITALIZED_INCOME_STRATEGY_PARAM_NAME, LoanCapitalizedIncomeStrategy.class);
        final LoanCapitalizedIncomeType capitalizedIncomeType = command
                .enumValueOfParameterNamed(LoanProductConstants.CAPITALIZED_INCOME_TYPE_PARAM_NAME, LoanCapitalizedIncomeType.class);

        final boolean enableBuyDownFee = command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.ENABLE_BUY_DOWN_FEE_PARAM_NAME);
        final LoanBuyDownFeeCalculationType buyDownFeeCalculationType = command.enumValueOfParameterNamed(
                LoanProductConstants.BUY_DOWN_FEE_CALCULATION_TYPE_PARAM_NAME, LoanBuyDownFeeCalculationType.class);
        final LoanBuyDownFeeStrategy buyDownFeeStrategy = command
                .enumValueOfParameterNamed(LoanProductConstants.BUY_DOWN_FEE_STRATEGY_PARAM_NAME, LoanBuyDownFeeStrategy.class);
        final LoanBuyDownFeeIncomeType buyDownFeeIncomeType = command
                .enumValueOfParameterNamed(LoanProductConstants.BUY_DOWN_FEE_INCOME_TYPE_PARAM_NAME, LoanBuyDownFeeIncomeType.class);
        final boolean merchantBuyDownFee = command
                .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.MERCHANT_BUY_DOWN_FEE_PARAM_NAME);

        return new LoanProduct(fund, loanTransactionProcessingStrategy, loanProductPaymentAllocationRules, loanProductCreditAllocationRules,
                name, shortName, description, currency, principal, minPrincipal, maxPrincipal, interestRatePerPeriod,
                minInterestRatePerPeriod, maxInterestRatePerPeriod, interestFrequencyType, annualInterestRate, interestMethod,
                interestCalculationPeriodMethod, allowPartialPeriodInterestCalcualtion, repaymentEvery, repaymentFrequencyType,
                numberOfRepayments, minNumberOfRepayments, maxNumberOfRepayments, graceOnPrincipalPayment,
                recurringMoratoriumOnPrincipalPeriods, graceOnInterestPayment, graceOnInterestCharged, amortizationMethod,
                inArrearsTolerance, productCharges, accountingRuleType, includeInBorrowerCycle, startDate, closeDate, externalId,
                useBorrowerCycle, loanProductBorrowerCycleVariations, multiDisburseLoan, maxTrancheCount, outstandingLoanBalance,
                graceOnArrearsAgeing, overdueDaysForNPA, daysInMonthType, daysInYearType, isInterestRecalculationEnabled,
                interestRecalculationSettings, minimumDaysBetweenDisbursalAndFirstRepayment, holdGuarantorFunds,
                loanProductGuaranteeDetails, principalThresholdForLastInstallment, accountMovesOutOfNPAOnlyOnArrearsCompletion,
                canDefineEmiAmount, installmentAmountInMultiplesOf, loanConfigurableAttributes, isLinkedToFloatingInterestRates,
                floatingRate, interestRateDifferential, minDifferentialLendingRate, maxDifferentialLendingRate,
                defaultDifferentialLendingRate, isFloatingInterestRateCalculationAllowed, isVariableInstallmentsAllowed,
                minimumGapBetweenInstallments, maximumGapBetweenInstallments, syncExpectedWithDisbursementDate, canUseForTopup,
                isEqualAmortization, productRates, fixedPrincipalPercentagePerInstallment, disallowExpectedDisbursements,
                allowApprovedDisbursedAmountsOverApplied, overAppliedCalculationType, overAppliedNumber, dueDaysForRepaymentEvent,
                overDueDaysForRepaymentEvent, enableDownPayment, disbursedAmountPercentageDownPayment, enableAutoRepaymentForDownPayment,
                repaymentStartDateType, enableInstallmentLevelDelinquency, loanScheduleType, loanScheduleProcessingType, fixedLength,
                enableAccrualActivityPosting, supportedInterestRefundTypes, chargeOffBehaviour, interestRecognitionOnDisbursementDate,
                daysInYearCustomStrategy, enableIncomeCapitalization, capitalizedIncomeCalculationType, capitalizedIncomeStrategy,
                capitalizedIncomeType, enableBuyDownFee, buyDownFeeCalculationType, buyDownFeeStrategy, buyDownFeeIncomeType,
                merchantBuyDownFee);
    }

    public LoanProduct assembleLoanProduct(final LoanProductRequestDTO loanProductRequestDTO) {

//      LoanProductDTO responseObject = LoanProductDTO.builder().build();
      LoanProductDTO.LoanProductDTOBuilder builder = LoanProductDTO.builder();
//      final String name = command.stringValueOfParameterNamed("name");
      final String name = loanProductRequestDTO.getName();
      builder.name(name);

//      final String shortName = command.stringValueOfParameterNamed(LoanProductConstants.SHORT_NAME);
      final String shortName = loanProductRequestDTO.getShortName();
      builder.shortName(shortName);

//      final String description = command.stringValueOfParameterNamed("description");
      final String description = loanProductRequestDTO.getDescription();
      builder.description(description);

//      final String currencyCode = command.stringValueOfParameterNamed("currencyCode");
      final String currencyCode = loanProductRequestDTO.getCurrency().getCode();

//      final Integer digitsAfterDecimal = command.integerValueOfParameterNamed("digitsAfterDecimal");
      final Integer digitsAfterDecimal = loanProductRequestDTO.getCurrency().getDecimalPlaces();

//      final Integer inMultiplesOf = command.integerValueOfParameterNamed("inMultiplesOf");
      final Integer inMultiplesOf = loanProductRequestDTO.getCurrency().getInMultiplesOf();

//      final MonetaryCurrency currency = new MonetaryCurrency(currencyCode, digitsAfterDecimal, inMultiplesOf);
      final MonetaryCurrencyDTO currency = MonetaryCurrencyDTO.builder()
          .code(currencyCode)
          .inMultiplesOf(inMultiplesOf)
          .digitsAfterDecimal(digitsAfterDecimal).build();

      final LoanProductRelatedDetailDTO.LoanProductRelatedDetailDTOBuilder loanProductRelatedBuilder = LoanProductRelatedDetailDTO.builder();
      loanProductRelatedBuilder.currency(currency);
//      builder.loanProductRelatedDetail(loanProductRelatedBuilder.build());

//      final BigDecimal principal = command.bigDecimalValueOfParameterNamed("principal");
      final BigDecimal principal = loanProductRequestDTO.getPrincipal();
      loanProductRelatedBuilder.principal(principal);

//      final BigDecimal minPrincipal = command.bigDecimalValueOfParameterNamed("minPrincipal");
      final BigDecimal minPrincipal = loanProductRequestDTO.getMinPrincipal();
      LoanProductMinMaxConstraintsDTO.LoanProductMinMaxConstraintsDTOBuilder loanProductMinMaxConstraintsDTOBuilder =
          LoanProductMinMaxConstraintsDTO.builder();
      loanProductMinMaxConstraintsDTOBuilder.minPrincipal(minPrincipal);

//      final BigDecimal maxPrincipal = command.bigDecimalValueOfParameterNamed("maxPrincipal");
      final BigDecimal maxPrincipal = loanProductRequestDTO.getMaxPrincipal();
      loanProductMinMaxConstraintsDTOBuilder.maxPrincipal(maxPrincipal);

//      final InterestMethod interestMethod = InterestMethod.fromInt(command.integerValueOfParameterNamed("interestType"));
      final InterestMethodENUM interestMethod = InterestMethodENUM.fromInt(loanProductRequestDTO.getInterestType().getId().intValue());
      loanProductRelatedBuilder.interestMethod(interestMethod);

//      final InterestCalculationPeriodMethod interestCalculationPeriodMethod = InterestCalculationPeriodMethod
//          .fromInt(command.integerValueOfParameterNamed("interestCalculationPeriodType"));
      final InterestCalculationPeriodMethodENUM interestCalculationPeriodMethod = InterestCalculationPeriodMethodENUM
          .fromInt(loanProductRequestDTO.getInterestCalculationPeriodType().getId().intValue());
      loanProductRelatedBuilder.interestCalculationPeriodMethod(interestCalculationPeriodMethod);


//      final boolean allowPartialPeriodInterestCalcualtion = command
//          .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.ALLOW_PARTIAL_PERIOD_INTEREST_CALCUALTION_PARAM_NAME);
      final boolean allowPartialPeriodInterestCalculation = loanProductRequestDTO.getAllowPartialPeriodInterestCalculation();
      loanProductRelatedBuilder.allowPartialPeriodInterestCalculation(allowPartialPeriodInterestCalculation);

//      final AmortizationMethod amortizationMethod = AmortizationMethod.fromInt(command.integerValueOfParameterNamed("amortizationType"));
      final AmortizationMethodENUM amortizationMethod = AmortizationMethodENUM.fromInt(loanProductRequestDTO.getAmortizationType().getId().intValue());
      loanProductRelatedBuilder.amortizationMethod(amortizationMethod);

//      final PeriodFrequencyType repaymentFrequencyType = PeriodFrequencyType
//          .fromInt(command.integerValueOfParameterNamed("repaymentFrequencyType"));
      final PeriodFrequencyTypeENUM repaymentFrequencyType = PeriodFrequencyTypeENUM
          .fromInt(loanProductRequestDTO.getRepaymentFrequencyType().getId().intValue());
      loanProductRelatedBuilder.repaymentPeriodFrequencyType(repaymentFrequencyType);


//      PeriodFrequencyType interestFrequencyType = PeriodFrequencyType.INVALID;
      PeriodFrequencyTypeENUM interestFrequencyType = PeriodFrequencyTypeENUM.INVALID;
      loanProductRelatedBuilder.interestPeriodFrequencyType(interestFrequencyType);

      BigDecimal interestRatePerPeriod = null;
      BigDecimal minInterestRatePerPeriod = null;
      BigDecimal maxInterestRatePerPeriod = null;
      BigDecimal annualInterestRate = null;
      BigDecimal interestRateDifferential = null;
      BigDecimal minDifferentialLendingRate = null;
      BigDecimal maxDifferentialLendingRate = null;
      BigDecimal defaultDifferentialLendingRate = null;
      Boolean isFloatingInterestRateCalculationAllowed = null;

      Integer minimumGapBetweenInstallments = null;
      Integer maximumGapBetweenInstallments = null;

      // Declaring this variable here to be used throughout the file
//      final DaysInYearType daysInYearType = DaysInYearType
//          .fromInt(command.integerValueOfParameterNamed(LoanProductConstants.DAYS_IN_YEAR_TYPE_PARAMETER_NAME));
      final DaysInYearTypeENUM daysInYearType = DaysInYearTypeENUM
          .fromInt(loanProductRequestDTO.getDaysInYearType().getId().intValue());
      loanProductRelatedBuilder.daysInYearType(daysInYearType.getValue());

//      final Integer repaymentEvery = command.integerValueOfParameterNamed("repaymentEvery");
      final Integer repaymentEvery = loanProductRequestDTO.getRepaymentEvery();
      loanProductRelatedBuilder.repayEvery(repaymentEvery);

//      final Integer numberOfRepayments = command.integerValueOfParameterNamed("numberOfRepayments");
      final Integer numberOfRepayments = loanProductRequestDTO.getNumberOfRepayments();
      loanProductRelatedBuilder.numberOfRepayments(numberOfRepayments);

//      final Boolean isLinkedToFloatingInterestRates = command.booleanObjectValueOfParameterNamed("isLinkedToFloatingInterestRates");
      final Boolean isLinkedToFloatingInterestRates = loanProductRequestDTO.getIsLinkedToFloatingInterestRates();
      builder.isLinkedToFloatingInterestRate(isLinkedToFloatingInterestRates);


      if (isLinkedToFloatingInterestRates != null && isLinkedToFloatingInterestRates) {
//        interestRateDifferential = command.bigDecimalValueOfParameterNamed("interestRateDifferential");
        interestRateDifferential = loanProductRequestDTO.getInterestRateDifferential();


//        minDifferentialLendingRate = command.bigDecimalValueOfParameterNamed("minDifferentialLendingRate");
        minDifferentialLendingRate = loanProductRequestDTO.getMinDifferentialLendingRate();

//        maxDifferentialLendingRate = command.bigDecimalValueOfParameterNamed("maxDifferentialLendingRate");
        maxDifferentialLendingRate = loanProductRequestDTO.getMaxDifferentialLendingRate();

//        defaultDifferentialLendingRate = command.bigDecimalValueOfParameterNamed("defaultDifferentialLendingRate");
        defaultDifferentialLendingRate = loanProductRequestDTO.getDefaultDifferentialLendingRate();

//        isFloatingInterestRateCalculationAllowed = command
//            .booleanObjectValueOfParameterNamed("isFloatingInterestRateCalculationAllowed");
        isFloatingInterestRateCalculationAllowed = loanProductRequestDTO.getIsFloatingInterestRateCalculationAllowed();
      } else {
//        interestFrequencyType = PeriodFrequencyType.fromInt(command.integerValueOfParameterNamed("interestRateFrequencyType"));
        interestFrequencyType = PeriodFrequencyTypeENUM.fromInt(loanProductRequestDTO.getInterestRateFrequencyType().getId().intValue());

//        interestRatePerPeriod = command.bigDecimalValueOfParameterNamed("interestRatePerPeriod");
        interestRatePerPeriod = loanProductRequestDTO.getInterestRatePerPeriod();

//        minInterestRatePerPeriod = command.bigDecimalValueOfParameterNamed("minInterestRatePerPeriod");
        minInterestRatePerPeriod = loanProductRequestDTO.getMinInterestRatePerPeriod();

//        maxInterestRatePerPeriod = command.bigDecimalValueOfParameterNamed("maxInterestRatePerPeriod");
        maxInterestRatePerPeriod = loanProductRequestDTO.getMaxInterestRatePerPeriod();

//        annualInterestRate = aprCalculator.calculateFrom(interestFrequencyType, interestRatePerPeriod, numberOfRepayments,
//            repaymentEvery, repaymentFrequencyType, daysInYearType);
        annualInterestRate = aprCalculator.calculateFrom(interestFrequencyType, interestRatePerPeriod, numberOfRepayments,
            repaymentEvery, repaymentFrequencyType, daysInYearType);
      }

//      final Boolean isVariableInstallmentsAllowed = command
//          .booleanObjectValueOfParameterNamed(LoanProductConstants.allowVariableInstallmentsParamName);
      final Boolean isVariableInstallmentsAllowed = loanProductRequestDTO.getAllowVariableInstallments();

      if (isVariableInstallmentsAllowed != null && isVariableInstallmentsAllowed) {
//        minimumGapBetweenInstallments = command.integerValueOfParameterNamed(LoanProductConstants.minimumGapBetweenInstallments);
        minimumGapBetweenInstallments = loanProductRequestDTO.getMinimumGap();

//        maximumGapBetweenInstallments = command.integerValueOfParameterNamed(LoanProductConstants.maximumGapBetweenInstallments);
        maximumGapBetweenInstallments = loanProductRequestDTO.getMaximumGap();
      }

//      final Integer minNumberOfRepayments = command.integerValueOfParameterNamed("minNumberOfRepayments");
      final Integer minNumberOfRepayments = loanProductRequestDTO.getMinNumberOfRepayments();

//      final Integer maxNumberOfRepayments = command.integerValueOfParameterNamed("maxNumberOfRepayments");
      final Integer maxNumberOfRepayments = loanProductRequestDTO.getMaxNumberOfRepayments();

//      final BigDecimal inArrearsTolerance = command.bigDecimalValueOfParameterNamed("inArrearsTolerance");
      final BigDecimal inArrearsTolerance = loanProductRequestDTO.getInArrearsTolerance();

      // grace details
//      final Integer graceOnPrincipalPayment = command.integerValueOfParameterNamed("graceOnPrincipalPayment");
      final Integer graceOnPrincipalPayment = loanProductRequestDTO.getGraceOnPrincipalPayment();

//      final Integer recurringMoratoriumOnPrincipalPeriods = command.integerValueOfParameterNamed("recurringMoratoriumOnPrincipalPeriods");
      final Integer recurringMoratoriumOnPrincipalPeriods = loanProductRequestDTO.getRecurringMoratoriumOnPrincipalPeriods();

//      final Integer graceOnInterestPayment = command.integerValueOfParameterNamed("graceOnInterestPayment");
      final Integer graceOnInterestPayment = loanProductRequestDTO.getGraceOnInterestPayment();

//      final Integer graceOnInterestCharged = command.integerValueOfParameterNamed("graceOnInterestCharged");
      final Integer graceOnInterestCharged = loanProductRequestDTO.getGraceOnInterestCharged();

//      final Integer minimumDaysBetweenDisbursalAndFirstRepayment = command
//          .integerValueOfParameterNamed(LoanProductConstants.MINIMUM_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT);
      final Integer minimumDaysBetweenDisbursalAndFirstRepayment = loanProductRequestDTO.getMinimumDaysBetweenDisbursalAndFirstRepayment();

//      final AccountingRuleType accountingRuleType = AccountingRuleType.fromInt(command.integerValueOfParameterNamed("accountingRule"));
      final AccountingRuleTypeENUM accountingRuleType = AccountingRuleTypeENUM.fromInt(loanProductRequestDTO.getAccountingRule().getId().intValue());

//      final boolean includeInBorrowerCycle = command.booleanPrimitiveValueOfParameterNamed("includeInBorrowerCycle");
      final boolean includeInBorrowerCycle = loanProductRequestDTO.getIncludeInBorrowerCycle();

//      final LocalDate startDate = command.localDateValueOfParameterNamed("startDate");
      final LocalDate startDate = loanProductRequestDTO.getStartDate();

//      final LocalDate closeDate = command.localDateValueOfParameterNamed("closeDate");
      final LocalDate closeDate = loanProductRequestDTO.getCloseDate();

//      final ExternalId externalId = ExternalIdFactory.produce(command.stringValueOfParameterNamedAllowingNull("externalId"));
      final ExternalIdDTO externalId = ExternalIdFactory.produceDTO(loanProductRequestDTO.getExternalId());

      final LoanScheduleTypeENUM loanScheduleType;
//      if (command.hasParameter("loanScheduleType")) {
      if (loanProductRequestDTO.getLoanScheduleType() != null) {
//        loanScheduleType = LoanScheduleType.valueOf(command.stringValueOfParameterNamed("loanScheduleType"));
        loanScheduleType = LoanScheduleTypeENUM.valueOf(loanProductRequestDTO.getLoanScheduleType().getCode());
      } else {
        // For backward compatibility
//        loanScheduleType = LoanScheduleType.CUMULATIVE;
        loanScheduleType = LoanScheduleTypeENUM.CUMULATIVE;
      }

      final LoanScheduleProcessingTypeENUM loanScheduleProcessingType;
//      if (LoanScheduleType.PROGRESSIVE.equals(loanScheduleType) && command.hasParameter("loanScheduleProcessingType")) {
      if (LoanScheduleTypeENUM.PROGRESSIVE.equals(loanScheduleType) && loanProductRequestDTO.getLoanScheduleProcessingType() != null) {
//        loanScheduleProcessingType = LoanScheduleProcessingType
//            .valueOf(command.stringValueOfParameterNamed("loanScheduleProcessingType"));
        loanScheduleProcessingType = LoanScheduleProcessingTypeENUM
            .valueOf(loanProductRequestDTO.getLoanScheduleProcessingType().getCode());
      } else {
        // For backward compatibility
//        loanScheduleProcessingType = LoanScheduleProcessingType.HORIZONTAL;
        loanScheduleProcessingType = LoanScheduleProcessingTypeENUM.HORIZONTAL;
      }

//      final boolean useBorrowerCycle = command
//          .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.USE_BORROWER_CYCLE_PARAMETER_NAME);
      final boolean useBorrowerCycle = loanProductRequestDTO.getUseBorrowerCycle();

      Set<LoanProductBorrowerCycleVariationsDTO> loanProductBorrowerCycleVariations = new HashSet<>();

      if (useBorrowerCycle) {
//        populateBorrowerCycleVariations(command, loanProductBorrowerCycleVariations);
        loanProductBorrowerCycleVariations = populateBorrowerCycleVariationsDTO(loanProductRequestDTO);
      }

//      final boolean multiDisburseLoan = command
//          .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.MULTI_DISBURSE_LOAN_PARAMETER_NAME);
      final boolean multiDisburseLoan = loanProductRequestDTO.getMultiDisburseLoan();

      Integer maxTrancheCount = null;
      BigDecimal outstandingLoanBalance = null;
      if (multiDisburseLoan) {
//        outstandingLoanBalance = command.bigDecimalValueOfParameterNamed(LoanProductConstants.OUTSTANDING_LOAN_BALANCE_PARAMETER_NAME);
        outstandingLoanBalance = loanProductRequestDTO.getOutstandingLoanBalance();

//        maxTrancheCount = command.integerValueOfParameterNamed(LoanProductConstants.MAX_TRANCHE_COUNT_PARAMETER_NAME);
        maxTrancheCount = loanProductRequestDTO.getMaxTrancheCount();
      }

//      final Integer graceOnArrearsAgeing = command
//          .integerValueOfParameterNamed(LoanProductConstants.GRACE_ON_ARREARS_AGEING_PARAMETER_NAME);
      final Integer graceOnArrearsAgeing = loanProductRequestDTO.getGraceOnArrearsAgeing();

//      final Integer overdueDaysForNPA = command.integerValueOfParameterNamed(LoanProductConstants.OVERDUE_DAYS_FOR_NPA_PARAMETER_NAME);
      final Integer overdueDaysForNPA = loanProductRequestDTO.getOverdueDaysForNPA();

      // Interest recalculation settings
//      final boolean isInterestRecalculationEnabled = command
//          .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.IS_INTEREST_RECALCULATION_ENABLED_PARAMETER_NAME);
      final boolean isInterestRecalculationEnabled = loanProductRequestDTO.getIsInterestRecalculationEnabled();

//      final DaysInMonthType daysInMonthType = DaysInMonthType
//          .fromInt(command.integerValueOfParameterNamed(LoanProductConstants.DAYS_IN_MONTH_TYPE_PARAMETER_NAME));
      final DaysInMonthTypeENUM daysInMonthType = DaysInMonthTypeENUM
          .fromInt(loanProductRequestDTO.getDaysInMonthType().getId().intValue());

//      final DaysInYearCustomStrategyType daysInYearCustomStrategy = command.enumValueOfParameterNamed(
//          LoanProductConstants.DAYS_IN_YEAR_CUSTOM_STRATEGY_TYPE_PARAMETER_NAME, DaysInYearCustomStrategyType.class);
      final DaysInYearCustomStrategyTypeENUM daysInYearCustomStrategy = DaysInYearCustomStrategyTypeENUM.fromInt(Integer.valueOf(loanProductRequestDTO.getDaysInYearCustomStrategy().getId()));

      LoanProductInterestRecalculationDetailsDTO interestRecalculationSettings = null;

      if (isInterestRecalculationEnabled) {
//        interestRecalculationSettings = interestRecalculationDetailsAssembler.createFrom(command);
        interestRecalculationSettings = interestRecalculationDetailsAssembler.createFromDTO(loanProductRequestDTO);
      }

//      final boolean holdGuarantorFunds = command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.holdGuaranteeFundsParamName);
      final boolean holdGuarantorFunds = loanProductRequestDTO.getHoldGuaranteeFunds();

      LoanProductGuaranteeDetailsDTO loanProductGuaranteeDetails = null;

      if (holdGuarantorFunds) {
//        loanProductGuaranteeDetails = guaranteeDetailsAssembler.createFrom(command);
        loanProductGuaranteeDetails = guaranteeDetailsAssembler.createFromDTO(loanProductRequestDTO);
      }

      LoanProductConfigurableAttributesDTO loanConfigurableAttributes = null;
//      if (command.parameterExists(LoanProductConstants.allowAttributeOverridesParamName)) {
      if (loanProductRequestDTO.getAllowAttributeOverrides() != null) {
//        loanConfigurableAttributes = LoanProductConfigurableAttributes.createFrom(command);
        loanConfigurableAttributes = LoanProductConfigurableAttributesDTO.createFrom(loanProductRequestDTO);
      } else {
//        loanConfigurableAttributes = LoanProductConfigurableAttributes.populateDefaultsForConfigurableAttributes();
        loanConfigurableAttributes = LoanProductConfigurableAttributesDTO.populateDefaultsForConfigurableAttributes();
      }

//      BigDecimal principalThresholdForLastInstallment = command
//          .bigDecimalValueOfParameterNamed(LoanProductConstants.principalThresholdForLastInstallmentParamName);
      BigDecimal principalThresholdForLastInstallment = loanProductRequestDTO.getPrincipalThresholdForLastInstallment();

      if (principalThresholdForLastInstallment == null) {
        principalThresholdForLastInstallment = multiDisburseLoan
            ? LoanProductConstants.DEFAULT_PRINCIPAL_THRESHOLD_FOR_MULTI_DISBURSE_LOAN
            : LoanProductConstants.DEFAULT_PRINCIPAL_THRESHOLD_FOR_SINGLE_DISBURSE_LOAN;
      }
//      final boolean accountMovesOutOfNPAOnlyOnArrearsCompletion = command
//          .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.ACCOUNT_MOVES_OUT_OF_NPA_ONLY_ON_ARREARS_COMPLETION_PARAM_NAME);
      final boolean accountMovesOutOfNPAOnlyOnArrearsCompletion = loanProductRequestDTO.getAccountMovesOutOfNPAOnlyOnArrearsCompletion();

//      final boolean canDefineEmiAmount = command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.canDefineEmiAmountParamName);
      final boolean canDefineEmiAmount = loanProductRequestDTO.getCanDefineInstallmentAmount();

//      final Integer installmentAmountInMultiplesOf = command
//          .integerValueOfParameterNamed(LoanProductConstants.installmentAmountInMultiplesOfParamName);
      final Integer installmentAmountInMultiplesOf = loanProductRequestDTO.getInstallmentAmountInMultiplesOf();

//      final boolean syncExpectedWithDisbursementDate = command.booleanPrimitiveValueOfParameterNamed("syncExpectedWithDisbursementDate");
      final boolean syncExpectedWithDisbursementDate = loanProductRequestDTO.getSyncExpectedWithDisbursementDate();

//      final boolean canUseForTopup = command.parameterExists(LoanProductConstants.CAN_USE_FOR_TOPUP)
//          && command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.CAN_USE_FOR_TOPUP);
      final boolean canUseForTopup = loanProductRequestDTO.getCanUseForTopup();

//      final boolean isEqualAmortization = command.parameterExists(LoanProductConstants.IS_EQUAL_AMORTIZATION_PARAM)
//          && command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.IS_EQUAL_AMORTIZATION_PARAM);
      final boolean isEqualAmortization = loanProductRequestDTO.getIsEqualAmortization();

//      BigDecimal fixedPrincipalPercentagePerInstallment = command
//          .bigDecimalValueOfParameterNamed(LoanProductConstants.fixedPrincipalPercentagePerInstallmentParamName);
      BigDecimal fixedPrincipalPercentagePerInstallment = loanProductRequestDTO.getFixedPrincipalPercentagePerInstallment();

//      final boolean disallowExpectedDisbursements = command.parameterExists(LoanProductConstants.DISALLOW_EXPECTED_DISBURSEMENTS)
//          && command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.DISALLOW_EXPECTED_DISBURSEMENTS);
      final boolean disallowExpectedDisbursements = loanProductRequestDTO.getDisallowExpectedDisbursements();

//      final boolean allowApprovedDisbursedAmountsOverApplied = command
//          .parameterExists(LoanProductConstants.ALLOW_APPROVED_DISBURSED_AMOUNTS_OVER_APPLIED)
//          && command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.ALLOW_APPROVED_DISBURSED_AMOUNTS_OVER_APPLIED);
      final boolean allowApprovedDisbursedAmountsOverApplied = loanProductRequestDTO.getAllowApprovedDisbursedAmountsOverApplied();

//      final String overAppliedCalculationType = command
//          .stringValueOfParameterNamedAllowingNull(LoanProductConstants.OVER_APPLIED_CALCULATION_TYPE);
      final String overAppliedCalculationType = loanProductRequestDTO.getOverAppliedCalculationType();

//      final Integer overAppliedNumber = command.integerValueOfParameterNamed(LoanProductConstants.OVER_APPLIED_NUMBER);
      final Integer overAppliedNumber = loanProductRequestDTO.getOverAppliedNumber();

//      final Integer dueDaysForRepaymentEvent = command.integerValueOfParameterNamed(LoanProductConstants.DUE_DAYS_FOR_REPAYMENT_EVENT);
      final Integer dueDaysForRepaymentEvent = loanProductRequestDTO.getDueDaysForRepaymentEvent();

//      final Integer overDueDaysForRepaymentEvent = command
//          .integerValueOfParameterNamed(LoanProductConstants.OVER_DUE_DAYS_FOR_REPAYMENT_EVENT);
      final Integer overDueDaysForRepaymentEvent = loanProductRequestDTO.getOverDueDaysForRepaymentEvent();

//      final boolean enableDownPayment = command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.ENABLE_DOWN_PAYMENT);
      final boolean enableDownPayment = loanProductRequestDTO.getEnableDownPayment();

//      final BigDecimal disbursedAmountPercentageDownPayment = command
//          .bigDecimalValueOfParameterNamed(LoanProductConstants.DISBURSED_AMOUNT_PERCENTAGE_DOWN_PAYMENT);
      final BigDecimal disbursedAmountPercentageDownPayment = loanProductRequestDTO.getDisbursedAmountPercentageForDownPayment();

//      final boolean enableAutoRepaymentForDownPayment = command
//          .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.ENABLE_AUTO_REPAYMENT_DOWN_PAYMENT);
      final boolean enableAutoRepaymentForDownPayment = loanProductRequestDTO.getEnableAutoRepaymentForDownPayment();

//      final RepaymentStartDateType repaymentStartDateType = RepaymentStartDateType
//          .fromInt(command.integerValueOfParameterNamed(LoanProductConstants.REPAYMENT_START_DATE_TYPE));
      final RepaymentStartDateTypeENUM repaymentStartDateType = RepaymentStartDateTypeENUM.fromInt(loanProductRequestDTO.getRepaymentStartDateType().getId().intValue());

//      final boolean enableInstallmentLevelDelinquency = command
//          .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.ENABLE_INSTALLMENT_LEVEL_DELINQUENCY);
      final boolean enableInstallmentLevelDelinquency = loanProductRequestDTO.getEnableInstallmentLevelDelinquency();

//      final Integer fixedLength = command.integerValueOfParameterNamed(LoanProductConstants.FIXED_LENGTH);
      final Integer fixedLength = loanProductRequestDTO.getFixedLength();

//      final boolean enableAccrualActivityPosting = command
//          .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.ENABLE_ACCRUAL_ACTIVITY_POSTING);
      final boolean enableAccrualActivityPosting = loanProductRequestDTO.getEnableAccrualActivityPosting();

      boolean interestRecognitionOnDisbursementDate = false;
//      if (command.parameterExists(LoanProductConstants.INTEREST_RECOGNITION_ON_DISBURSEMENT_DATE)) {
      if (Boolean.TRUE.equals(loanProductRequestDTO.getInterestRecognitionOnDisbursementDate())) {
//        interestRecognitionOnDisbursementDate = command
//            .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.INTEREST_RECOGNITION_ON_DISBURSEMENT_DATE);
        interestRecognitionOnDisbursementDate = loanProductRequestDTO.getInterestRecognitionOnDisbursementDate();
      }

      List<LoanSupportedInterestRefundTypesENUM> supportedInterestRefundTypes = new ArrayList<>();
//      if (command.parameterExists(LoanProductConstants.SUPPORTED_INTEREST_REFUND_TYPES)) {
      if (loanProductRequestDTO.getSupportedInterestRefundTypes() != null) {
//        JsonArray supportedTransactionsForInterestRefund = command
//            .arrayOfParameterNamed(LoanProductConstants.SUPPORTED_INTEREST_REFUND_TYPES);
        List<StringEnumOptionData> supportedTransactionsForInterestRefund = loanProductRequestDTO.getSupportedInterestRefundTypes();
//        supportedTransactionsForInterestRefund.iterator().forEachRemaining(value -> {
//          supportedInterestRefundTypes.add(LoanSupportedInterestRefundTypes.valueOf(value.getAsString()));
//        });

        for(StringEnumOptionData element: supportedTransactionsForInterestRefund){
          supportedInterestRefundTypes.add(LoanSupportedInterestRefundTypesENUM.valueOf(element.getValue()));
        }
      }

      final LoanChargeOffBehaviourENUM chargeOffBehaviour;
//      if (command.parameterExists(LoanProductConstants.CHARGE_OFF_BEHAVIOUR)) {
//        chargeOffBehaviour = LoanChargeOffBehaviour
//            .valueOf(command.stringValueOfParameterNamed(LoanProductConstants.CHARGE_OFF_BEHAVIOUR));
//      } else {
//        // For backward compatibility
//        chargeOffBehaviour = LoanChargeOffBehaviour.REGULAR;
//      }
      if (loanProductRequestDTO.getChargeOffBehaviour() != null) {
        chargeOffBehaviour = LoanChargeOffBehaviourENUM
            .valueOf(loanProductRequestDTO.getChargeOffBehaviour().getValue());
      } else {
        // For backward compatibility
//        chargeOffBehaviour = LoanChargeOffBehaviour.REGULAR;
        chargeOffBehaviour = LoanChargeOffBehaviourENUM.REGULAR;
      }

//      final boolean enableIncomeCapitalization = command
//          .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.ENABLE_INCOME_CAPITALIZATION_PARAM_NAME);
      final boolean enableIncomeCapitalization = loanProductRequestDTO.getEnableIncomeCapitalization();

//      final LoanCapitalizedIncomeCalculationType capitalizedIncomeCalculationType = command.enumValueOfParameterNamed(
//          LoanProductConstants.CAPITALIZED_INCOME_CALCULATION_TYPE_PARAM_NAME, LoanCapitalizedIncomeCalculationType.class);
      final LoanCapitalizedIncomeCalculationTypeENUM capitalizedIncomeCalculationType = LoanCapitalizedIncomeCalculationTypeENUM.fromInt(loanProductRequestDTO.getCapitalizedIncomeCalculationType().getCode());

//      final LoanCapitalizedIncomeStrategy capitalizedIncomeStrategy = command.enumValueOfParameterNamed(
//          LoanProductConstants.CAPITALIZED_INCOME_STRATEGY_PARAM_NAME, LoanCapitalizedIncomeStrategy.class);
      final LoanCapitalizedIncomeStrategyENUM capitalizedIncomeStrategy = LoanCapitalizedIncomeStrategyENUM.fromInt(loanProductRequestDTO.getCapitalizedIncomeStrategy().getCode());

//      final LoanCapitalizedIncomeType capitalizedIncomeType = command
//          .enumValueOfParameterNamed(LoanProductConstants.CAPITALIZED_INCOME_TYPE_PARAM_NAME, LoanCapitalizedIncomeType.class);
      final LoanCapitalizedIncomeTypeENUM capitalizedIncomeType = LoanCapitalizedIncomeTypeENUM.fromCode(loanProductRequestDTO.getCapitalizedIncomeType().getCode());

//      final boolean enableBuyDownFee = command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.ENABLE_BUY_DOWN_FEE_PARAM_NAME);
      final boolean enableBuyDownFee = loanProductRequestDTO.getEnableBuyDownFee();

//      final LoanBuyDownFeeCalculationType buyDownFeeCalculationType = command.enumValueOfParameterNamed(
//          LoanProductConstants.BUY_DOWN_FEE_CALCULATION_TYPE_PARAM_NAME, LoanBuyDownFeeCalculationType.class);
      final LoanBuyDownFeeCalculationTypeENUM buyDownFeeCalculationType = LoanBuyDownFeeCalculationTypeENUM.fromString(loanProductRequestDTO.getBuyDownFeeCalculationType().getCode());

//      final LoanBuyDownFeeStrategy buyDownFeeStrategy = command
//          .enumValueOfParameterNamed(LoanProductConstants.BUY_DOWN_FEE_STRATEGY_PARAM_NAME, LoanBuyDownFeeStrategy.class);
      final LoanBuyDownFeeStrategyENUM buyDownFeeStrategy = LoanBuyDownFeeStrategyENUM.fromString(loanProductRequestDTO.getBuyDownFeeStrategy().getCode());

//      final LoanBuyDownFeeIncomeType buyDownFeeIncomeType = command
//          .enumValueOfParameterNamed(LoanProductConstants.BUY_DOWN_FEE_INCOME_TYPE_PARAM_NAME, LoanBuyDownFeeIncomeType.class);
      final LoanBuyDownFeeIncomeTypeENUM buyDownFeeIncomeType = LoanBuyDownFeeIncomeTypeENUM.fromString(loanProductRequestDTO.getBuyDownFeeIncomeType().getCode());

//      final boolean merchantBuyDownFee = command
//          .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.MERCHANT_BUY_DOWN_FEE_PARAM_NAME);
      final boolean merchantBuyDownFee = loanProductRequestDTO.getMerchantBuyDownFee();

      return new LoanProduct(fund, loanTransactionProcessingStrategy, loanProductPaymentAllocationRules, loanProductCreditAllocationRules,
          name, shortName, description, currency, principal, minPrincipal, maxPrincipal, interestRatePerPeriod,
          minInterestRatePerPeriod, maxInterestRatePerPeriod, interestFrequencyType, annualInterestRate, interestMethod,
          interestCalculationPeriodMethod, allowPartialPeriodInterestCalcualtion, repaymentEvery, repaymentFrequencyType,
          numberOfRepayments, minNumberOfRepayments, maxNumberOfRepayments, graceOnPrincipalPayment,
          recurringMoratoriumOnPrincipalPeriods, graceOnInterestPayment, graceOnInterestCharged, amortizationMethod,
          inArrearsTolerance, productCharges, accountingRuleType, includeInBorrowerCycle, startDate, closeDate, externalId,
          useBorrowerCycle, loanProductBorrowerCycleVariations, multiDisburseLoan, maxTrancheCount, outstandingLoanBalance,
          graceOnArrearsAgeing, overdueDaysForNPA, daysInMonthType, daysInYearType, isInterestRecalculationEnabled,
          interestRecalculationSettings, minimumDaysBetweenDisbursalAndFirstRepayment, holdGuarantorFunds,
          loanProductGuaranteeDetails, principalThresholdForLastInstallment, accountMovesOutOfNPAOnlyOnArrearsCompletion,
          canDefineEmiAmount, installmentAmountInMultiplesOf, loanConfigurableAttributes, isLinkedToFloatingInterestRates,
          floatingRate, interestRateDifferential, minDifferentialLendingRate, maxDifferentialLendingRate,
          defaultDifferentialLendingRate, isFloatingInterestRateCalculationAllowed, isVariableInstallmentsAllowed,
          minimumGapBetweenInstallments, maximumGapBetweenInstallments, syncExpectedWithDisbursementDate, canUseForTopup,
          isEqualAmortization, productRates, fixedPrincipalPercentagePerInstallment, disallowExpectedDisbursements,
          allowApprovedDisbursedAmountsOverApplied, overAppliedCalculationType, overAppliedNumber, dueDaysForRepaymentEvent,
          overDueDaysForRepaymentEvent, enableDownPayment, disbursedAmountPercentageDownPayment, enableAutoRepaymentForDownPayment,
          repaymentStartDateType, enableInstallmentLevelDelinquency, loanScheduleType, loanScheduleProcessingType, fixedLength,
          enableAccrualActivityPosting, supportedInterestRefundTypes, chargeOffBehaviour, interestRecognitionOnDisbursementDate,
          daysInYearCustomStrategy, enableIncomeCapitalization, capitalizedIncomeCalculationType, capitalizedIncomeStrategy,
          capitalizedIncomeType, enableBuyDownFee, buyDownFeeCalculationType, buyDownFeeStrategy, buyDownFeeIncomeType,
          merchantBuyDownFee);




    }

    private void populateBorrowerCycleVariations(final JsonCommand command,
            final Set<LoanProductBorrowerCycleVariations> loanProductBorrowerCycleVariations) {
        assemblePrincipalVariations(command, loanProductBorrowerCycleVariations);

        assembleRepaymentVariations(command, loanProductBorrowerCycleVariations);

        assembleInterestRateVariations(command, loanProductBorrowerCycleVariations);
    }

    private Set<LoanProductBorrowerCycleVariationsDTO> populateBorrowerCycleVariationsDTO(LoanProductRequestDTO loanProductRequestDTO) {
      Set<LoanProductBorrowerCycleVariationsDTO> result1 = assemblePrincipalVariationsDTO(loanProductRequestDTO);
      Set<LoanProductBorrowerCycleVariationsDTO> result2 = assembleRepaymentVariationsDTO(loanProductRequestDTO);
      Set<LoanProductBorrowerCycleVariationsDTO> result3 = assembleInterestRateVariationsDTO(loanProductRequestDTO);

      result1.addAll(result2);
      result1.addAll(result3);
      return result1;
    }

    private void assembleInterestRateVariations(final JsonCommand command,
            final Set<LoanProductBorrowerCycleVariations> loanProductBorrowerCycleVariations) {
        assembleVariations(command, loanProductBorrowerCycleVariations, LoanProductParamType.INTERESTRATE.getValue(),
                LoanProductConstants.INTEREST_RATE_VARIATIONS_FOR_BORROWER_CYCLE_PARAMETER_NAME);

    }

    private void assembleRepaymentVariations(final JsonCommand command,
            final Set<LoanProductBorrowerCycleVariations> loanProductBorrowerCycleVariations) {
        assembleVariations(command, loanProductBorrowerCycleVariations, LoanProductParamType.REPAYMENT.getValue(),
                LoanProductConstants.NUMBER_OF_REPAYMENT_VARIATIONS_FOR_BORROWER_CYCLE_PARAMETER_NAME);

    }

    private void assemblePrincipalVariations(final JsonCommand command,
            final Set<LoanProductBorrowerCycleVariations> loanProductBorrowerCycleVariations) {
        assembleVariations(command, loanProductBorrowerCycleVariations, LoanProductParamType.PRINCIPAL.getValue(),
                LoanProductConstants.PRINCIPAL_VARIATIONS_FOR_BORROWER_CYCLE_PARAMETER_NAME);
    }

    private void assembleVariations(final JsonCommand command,
            final Set<LoanProductBorrowerCycleVariations> loanProductBorrowerCycleVariations, Integer paramType,
            String variationParameterName) {
        if (command.parameterExists(variationParameterName)) {
            final JsonArray variationArray = command.arrayOfParameterNamed(variationParameterName);
            if (variationArray != null && variationArray.size() > 0) {
                int i = 0;
                do {
                    final JsonObject jsonObject = variationArray.get(i).getAsJsonObject();
                    BigDecimal defaultValue = null;
                    BigDecimal minValue = null;
                    BigDecimal maxValue = null;
                    Integer cycleNumber = null;
                    Integer valueUsageCondition = null;
                    if (jsonObject.has(LoanProductConstants.DEFAULT_VALUE_PARAMETER_NAME)
                            && jsonObject.get(LoanProductConstants.DEFAULT_VALUE_PARAMETER_NAME).isJsonPrimitive()) {
                        defaultValue = jsonObject.getAsJsonPrimitive(LoanProductConstants.DEFAULT_VALUE_PARAMETER_NAME).getAsBigDecimal();
                    }
                    if (jsonObject.has(LoanProductConstants.MIN_VALUE_PARAMETER_NAME)
                            && jsonObject.get(LoanProductConstants.MIN_VALUE_PARAMETER_NAME).isJsonPrimitive()
                            && StringUtils.isNotBlank(jsonObject.get(LoanProductConstants.MIN_VALUE_PARAMETER_NAME).getAsString())) {
                        minValue = jsonObject.getAsJsonPrimitive(LoanProductConstants.MIN_VALUE_PARAMETER_NAME).getAsBigDecimal();
                    }
                    if (jsonObject.has(LoanProductConstants.MAX_VALUE_PARAMETER_NAME)
                            && jsonObject.get(LoanProductConstants.MAX_VALUE_PARAMETER_NAME).isJsonPrimitive()
                            && StringUtils.isNotBlank(jsonObject.get(LoanProductConstants.MAX_VALUE_PARAMETER_NAME).getAsString())) {
                        maxValue = jsonObject.getAsJsonPrimitive(LoanProductConstants.MAX_VALUE_PARAMETER_NAME).getAsBigDecimal();
                    }
                    if (jsonObject.has(LoanProductConstants.BORROWER_CYCLE_NUMBER_PARAM_NAME)
                            && jsonObject.get(LoanProductConstants.BORROWER_CYCLE_NUMBER_PARAM_NAME).isJsonPrimitive()) {
                        cycleNumber = jsonObject.getAsJsonPrimitive(LoanProductConstants.BORROWER_CYCLE_NUMBER_PARAM_NAME).getAsInt();
                    }
                    if (jsonObject.has(LoanProductConstants.VALUE_CONDITION_TYPE_PARAM_NAME)
                            && jsonObject.get(LoanProductConstants.VALUE_CONDITION_TYPE_PARAM_NAME).isJsonPrimitive()) {
                        valueUsageCondition = jsonObject.getAsJsonPrimitive(LoanProductConstants.VALUE_CONDITION_TYPE_PARAM_NAME)
                                .getAsInt();
                    }
                    LoanProductBorrowerCycleVariations borrowerCycleVariations = new LoanProductBorrowerCycleVariations(cycleNumber,
                            paramType, valueUsageCondition, minValue, maxValue, defaultValue);
                    loanProductBorrowerCycleVariations.add(borrowerCycleVariations);
                    i++;
                } while (i < variationArray.size());
            }
        }
    }

    private Set<LoanProductBorrowerCycleVariationsDTO> assemblePrincipalVariationsDTO(LoanProductRequestDTO loanProductRequestDTO) {
      Set<LoanProductBorrowerCycleVariationsDTO> loanProductBorrowerCycleVariations = new HashSet<>();
      if (!loanProductRequestDTO.getPrincipalVariationsForBorrowerCycle().isEmpty()) {
//        final JsonArray variationArray = command.arrayOfParameterNamed(variationParameterName);
//        if (loanProductRequestDTO.getPrincipalVariationsForBorrowerCycle().size() > 0) {
//          int i = 0;
//          do {
////            final JsonObject jsonObject = variationArray.get(i).getAsJsonObject();
//            BigDecimal defaultValue = null;
//            BigDecimal minValue = null;
//            BigDecimal maxValue = null;
//            Integer cycleNumber = null;
//            Integer valueUsageCondition = null;
//            if (jsonObject.has(LoanProductConstants.DEFAULT_VALUE_PARAMETER_NAME)
//                && jsonObject.get(LoanProductConstants.DEFAULT_VALUE_PARAMETER_NAME).isJsonPrimitive()) {
//              defaultValue = jsonObject.getAsJsonPrimitive(LoanProductConstants.DEFAULT_VALUE_PARAMETER_NAME).getAsBigDecimal();
//            }
//            if (jsonObject.has(LoanProductConstants.MIN_VALUE_PARAMETER_NAME)
//                && jsonObject.get(LoanProductConstants.MIN_VALUE_PARAMETER_NAME).isJsonPrimitive()
//                && StringUtils.isNotBlank(jsonObject.get(LoanProductConstants.MIN_VALUE_PARAMETER_NAME).getAsString())) {
//              minValue = jsonObject.getAsJsonPrimitive(LoanProductConstants.MIN_VALUE_PARAMETER_NAME).getAsBigDecimal();
//            }
//            if (jsonObject.has(LoanProductConstants.MAX_VALUE_PARAMETER_NAME)
//                && jsonObject.get(LoanProductConstants.MAX_VALUE_PARAMETER_NAME).isJsonPrimitive()
//                && StringUtils.isNotBlank(jsonObject.get(LoanProductConstants.MAX_VALUE_PARAMETER_NAME).getAsString())) {
//              maxValue = jsonObject.getAsJsonPrimitive(LoanProductConstants.MAX_VALUE_PARAMETER_NAME).getAsBigDecimal();
//            }
//            if (jsonObject.has(LoanProductConstants.BORROWER_CYCLE_NUMBER_PARAM_NAME)
//                && jsonObject.get(LoanProductConstants.BORROWER_CYCLE_NUMBER_PARAM_NAME).isJsonPrimitive()) {
//              cycleNumber = jsonObject.getAsJsonPrimitive(LoanProductConstants.BORROWER_CYCLE_NUMBER_PARAM_NAME).getAsInt();
//            }
//            if (jsonObject.has(LoanProductConstants.VALUE_CONDITION_TYPE_PARAM_NAME)
//                && jsonObject.get(LoanProductConstants.VALUE_CONDITION_TYPE_PARAM_NAME).isJsonPrimitive()) {
//              valueUsageCondition = jsonObject.getAsJsonPrimitive(LoanProductConstants.VALUE_CONDITION_TYPE_PARAM_NAME)
//                  .getAsInt();
//            }
//            LoanProductBorrowerCycleVariations borrowerCycleVariations = new LoanProductBorrowerCycleVariations(cycleNumber,
//                paramType, valueUsageCondition, minValue, maxValue, defaultValue);
//            loanProductBorrowerCycleVariations.add(borrowerCycleVariations);
//            i++;
//          } while (i < variationArray.size());
//        }

        for(LoanProductBorrowerCycleVariationDataDTO element :loanProductRequestDTO.getPrincipalVariationsForBorrowerCycle()) {
          BigDecimal defaultValue = null;
          BigDecimal minValue = null;
          BigDecimal maxValue = null;
          Integer cycleNumber = null;
          Integer valueUsageCondition = null;
//          if (jsonObject.has(LoanProductConstants.DEFAULT_VALUE_PARAMETER_NAME)
//              && jsonObject.get(LoanProductConstants.DEFAULT_VALUE_PARAMETER_NAME).isJsonPrimitive()) {
          if (element.getDefaultValue() != null) {
            defaultValue = element.getDefaultValue();
          }
//          if (jsonObject.has(LoanProductConstants.MIN_VALUE_PARAMETER_NAME)
//              && jsonObject.get(LoanProductConstants.MIN_VALUE_PARAMETER_NAME).isJsonPrimitive()
//              && StringUtils.isNotBlank(jsonObject.get(LoanProductConstants.MIN_VALUE_PARAMETER_NAME).getAsString())) {

          if (element.getMinValue() != null) {
//              minValue = jsonObject.getAsJsonPrimitive(LoanProductConstants.MIN_VALUE_PARAMETER_NAME).getAsBigDecimal();
              minValue = element.getMinValue();
          }
//          if (jsonObject.has(LoanProductConstants.MAX_VALUE_PARAMETER_NAME)
//              && jsonObject.get(LoanProductConstants.MAX_VALUE_PARAMETER_NAME).isJsonPrimitive()
//              && StringUtils.isNotBlank(jsonObject.get(LoanProductConstants.MAX_VALUE_PARAMETER_NAME).getAsString())) {
          if (element.getMaxValue() != null) {
//              maxValue = jsonObject.getAsJsonPrimitive(LoanProductConstants.MAX_VALUE_PARAMETER_NAME).getAsBigDecimal();
              maxValue = element.getMaxValue();
          }
//          if (jsonObject.has(LoanProductConstants.BORROWER_CYCLE_NUMBER_PARAM_NAME)
//              && jsonObject.get(LoanProductConstants.BORROWER_CYCLE_NUMBER_PARAM_NAME).isJsonPrimitive()) {
//            cycleNumber = jsonObject.getAsJsonPrimitive(LoanProductConstants.BORROWER_CYCLE_NUMBER_PARAM_NAME).getAsInt();
//          }
          if (element.getBorrowerCycleNumber() != null) {
            cycleNumber = element.getBorrowerCycleNumber();
          }
//          if (jsonObject.has(LoanProductConstants.VALUE_CONDITION_TYPE_PARAM_NAME)
//              && jsonObject.get(LoanProductConstants.VALUE_CONDITION_TYPE_PARAM_NAME).isJsonPrimitive()) {
          if (element.getValueConditionType() != null) {
              valueUsageCondition = element.getValueConditionType().getId().intValue();
          }
//          LoanProductBorrowerCycleVariations borrowerCycleVariations = new LoanProductBorrowerCycleVariations(cycleNumber,
//              paramType, valueUsageCondition, minValue, maxValue, defaultValue);

          LoanProductBorrowerCycleVariationsDTO borrowerCycleVariations =
              LoanProductBorrowerCycleVariationsDTO.builder()
                  .borrowerCycleNumber(cycleNumber)
                  .paramType(element.getParamType().getId().intValue())
                  .valueConditionType(valueUsageCondition)
                  .minValue(minValue)
                  .maxValue(maxValue)
                  .defaultValue(defaultValue)
                  .build();
          loanProductBorrowerCycleVariations.add(borrowerCycleVariations);
        }
      }
      return loanProductBorrowerCycleVariations;
    }

    private Set<LoanProductBorrowerCycleVariationsDTO> assembleRepaymentVariationsDTO(LoanProductRequestDTO loanProductRequestDTO) {
      Set<LoanProductBorrowerCycleVariationsDTO> loanProductBorrowerCycleVariations = new HashSet<>();
      if (!loanProductRequestDTO.getNumberOfRepaymentVariationsForBorrowerCycle().isEmpty()) {
        for(LoanProductBorrowerCycleVariationDataDTO element :loanProductRequestDTO.getNumberOfRepaymentVariationsForBorrowerCycle()) {
          BigDecimal defaultValue = null;
          BigDecimal minValue = null;
          BigDecimal maxValue = null;
          Integer cycleNumber = null;
          Integer valueUsageCondition = null;
          if (element.getDefaultValue() != null) {
            defaultValue = element.getDefaultValue();
          }
          if (element.getMinValue() != null) {
            minValue = element.getMinValue();
          }
          if (element.getMaxValue() != null) {
            maxValue = element.getMaxValue();
          }
          if (element.getBorrowerCycleNumber() != null) {
            cycleNumber = element.getBorrowerCycleNumber();
          }
          if (element.getValueConditionType() != null) {
            valueUsageCondition = element.getValueConditionType().getId().intValue();
          }
          LoanProductBorrowerCycleVariationsDTO borrowerCycleVariations =
              LoanProductBorrowerCycleVariationsDTO.builder()
                  .borrowerCycleNumber(cycleNumber)
                  .paramType(element.getParamType().getId().intValue())
                  .valueConditionType(valueUsageCondition)
                  .minValue(minValue)
                  .maxValue(maxValue)
                  .defaultValue(defaultValue)
                  .build();
          loanProductBorrowerCycleVariations.add(borrowerCycleVariations);
        }
      }
      return loanProductBorrowerCycleVariations;
    }

    private Set<LoanProductBorrowerCycleVariationsDTO> assembleInterestRateVariationsDTO(LoanProductRequestDTO loanProductRequestDTO) {
      Set<LoanProductBorrowerCycleVariationsDTO> loanProductBorrowerCycleVariations = new HashSet<>();
      if (!loanProductRequestDTO.getInterestRateVariationsForBorrowerCycle().isEmpty()) {
        for(LoanProductBorrowerCycleVariationDataDTO element :loanProductRequestDTO.getInterestRateVariationsForBorrowerCycle()) {
          BigDecimal defaultValue = null;
          BigDecimal minValue = null;
          BigDecimal maxValue = null;
          Integer cycleNumber = null;
          Integer valueUsageCondition = null;
          if (element.getDefaultValue() != null) {
            defaultValue = element.getDefaultValue();
          }
          if (element.getMinValue() != null) {
            minValue = element.getMinValue();
          }
          if (element.getMaxValue() != null) {
            maxValue = element.getMaxValue();
          }
          if (element.getBorrowerCycleNumber() != null) {
            cycleNumber = element.getBorrowerCycleNumber();
          }
          if (element.getValueConditionType() != null) {
            valueUsageCondition = element.getValueConditionType().getId().intValue();
          }
          LoanProductBorrowerCycleVariationsDTO borrowerCycleVariations =
              LoanProductBorrowerCycleVariationsDTO.builder()
                  .borrowerCycleNumber(cycleNumber)
                  .paramType(element.getParamType().getId().intValue())
                  .valueConditionType(valueUsageCondition)
                  .minValue(minValue)
                  .maxValue(maxValue)
                  .defaultValue(defaultValue)
                  .build();
          loanProductBorrowerCycleVariations.add(borrowerCycleVariations);
        }
      }
      return loanProductBorrowerCycleVariations;
    }
}
