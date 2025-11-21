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
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.accounting.common.AccountingRuleType;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
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
import org.apache.fineract.portfolio.loanproduct.data.LoanProductBorrowerCycleVariationsDTO;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductRequest;
import org.apache.fineract.portfolio.loanproduct.domain.AmortizationMethod;
import org.apache.fineract.portfolio.loanproduct.domain.InterestCalculationPeriodMethod;
import org.apache.fineract.portfolio.loanproduct.domain.InterestMethod;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductBorrowerCycleVariations;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductConfigurableAttributes;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductCreditAllocationRule;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductFloatingRates;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductGuaranteeDetails;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductInterestRecalculationDetails;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductMinMaxConstraints;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductParamType;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductPaymentAllocationRule;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductRelatedDetail;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductTrancheDetails;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductVariableInstallmentConfig;
import org.apache.fineract.portfolio.loanproduct.domain.LoanSupportedInterestRefundTypes;
import org.apache.fineract.portfolio.loanproduct.domain.RepaymentStartDateType;
import org.apache.fineract.portfolio.loanproduct.mapper.LoanProductBorrowerCycleVariationsToLoanProductBorrowCycleVariationsDTOMapper;
import org.apache.fineract.portfolio.rate.domain.Rate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanProductAssembler {

    private final LoanProductInterestRecalculationDetailsAssembler interestRecalculationDetailsAssembler;
    private final LoanProductGuaranteeDetailsAssembler guaranteeDetailsAssembler;
    private final AprCalculator aprCalculator;
    private final LoanProductBorrowerCycleVariationsToLoanProductBorrowCycleVariationsDTOMapper loanProductBorrowerCycleVariationsToLoanProductBorrowCycleVariationsDTOMapper;

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

    public LoanProduct assemble(final Map<String, Object> responseMap, final LoanProductRequest loanProductRequest) {
      final LoanProduct.LoanProductBuilder builder = LoanProduct.builder();
//      final LoanProductRelatedDetail.LoanProductRelatedDetailBuilder loanProductRelatedDetailBuilder = LoanProductRelatedDetail.builder();
//      final LoanProductMinMaxConstraints.LoanProductMinMaxConstraintsBuilder loanProductMinMaxConstraintsBuilder = LoanProductMinMaxConstraints.builder();

      builder.name(loanProductRequest.getName());
      builder.shortName(loanProductRequest.getShortName());
      builder.description(loanProductRequest.getDescription());
      builder.transactionProcessingStrategyCode(loanProductRequest.getTransactionProcessingStrategyCode());

//      final MonetaryCurrency currency = MonetaryCurrency.builder()
//          .code(loanProductRequest.getCurrencyCode())
//          .digitsAfterDecimal(loanProductRequest.getDigitsAfterDecimal())
//          .inMultiplesOf(loanProductRequest.getInMultiplesOf())
//          .build();
//      loanProductRelatedDetailBuilder.currency(currency);

      builder.loanProductMinMaxConstraints(loanProductMinMaxConstraints(loanProductRequest));

//      loanProductRelatedDetailBuilder.principal(loanProductRequest.getPrincipal());

//      final InterestMethod interestMethod = InterestMethod.fromInt(loanProductRequest.getInterestType());
//      loanProductRelatedDetailBuilder.interestMethod(interestMethod);

//      final InterestCalculationPeriodMethod interestCalculationPeriodMethod = InterestCalculationPeriodMethod
//          .fromInt(loanProductRequest.getInterestCalculationPeriod() == 0 ? 1 : loanProductRequest.getInterestCalculationPeriod());
//      loanProductRelatedDetailBuilder.interestCalculationPeriodMethod(interestCalculationPeriodMethod);

//      final Boolean allowPartialPeriodInterestCalculation = loanProductRequest.getAllowPartialPeriodInterestCalculation() != null ? loanProductRequest.getAllowPartialPeriodInterestCalculation() : Boolean.FALSE;
//      loanProductRelatedDetailBuilder.allowPartialPeriodInterestCalculation(allowPartialPeriodInterestCalculation);

//      final AmortizationMethod amortizationMethod = AmortizationMethod.fromInt(loanProductRequest.getAmortizationType());
//      loanProductRelatedDetailBuilder.amortizationMethod(amortizationMethod);

      final PeriodFrequencyType repaymentFrequencyType = PeriodFrequencyType
          .fromInt(loanProductRequest.getRepaymentFrequencyType());
//      loanProductRelatedDetailBuilder.repaymentPeriodFrequencyType(repaymentFrequencyType);

      PeriodFrequencyType interestFrequencyType = PeriodFrequencyType.INVALID;
//      loanProductRelatedDetailBuilder.interestPeriodFrequencyType(interestFrequencyType);

      BigDecimal interestRatePerPeriod = BigDecimal.ZERO;
      BigDecimal minInterestRatePerPeriod = BigDecimal.ZERO;
      BigDecimal maxInterestRatePerPeriod = BigDecimal.ZERO;
      BigDecimal annualInterestRate = BigDecimal.ZERO;
//      BigDecimal interestRateDifferential = BigDecimal.ZERO;
//      BigDecimal minDifferentialLendingRate = BigDecimal.ZERO;
//      BigDecimal maxDifferentialLendingRate = BigDecimal.ZERO;
//      BigDecimal defaultDifferentialLendingRate = BigDecimal.ZERO;
//      Boolean isFloatingInterestRateCalculationAllowed = null;
      Integer minimumGapBetweenInstallments = null;
      Integer maximumGapBetweenInstallments = null;

      // Declaring this variable here to be used throughout the file
      final DaysInYearType daysInYearType = DaysInYearType.fromInt(loanProductRequest.getDaysInYearType());
//      loanProductRelatedDetailBuilder.daysInYearType(loanProductRequest.getDaysInYearType());

      final Integer repaymentEvery = loanProductRequest.getRepaymentEvery() == null ? 1 : loanProductRequest.getRepaymentEvery();
//      loanProductRelatedDetailBuilder.repayEvery(repaymentEvery);

      final Integer numberOfRepayments = loanProductRequest.getNumberOfRepayments() == null ? 0 : loanProductRequest.getNumberOfRepayments();
//      loanProductRelatedDetailBuilder.numberOfRepayments(numberOfRepayments);

      final Boolean isLinkedToFloatingInterestRates = loanProductRequest.getIsLinkedToFloatingInterestRates() != null ?
          loanProductRequest.getIsLinkedToFloatingInterestRates() : Boolean.FALSE;
      builder.isLinkedToFloatingInterestRate(isLinkedToFloatingInterestRates);

      if (isLinkedToFloatingInterestRates) {
        LoanProductFloatingRates loanProductFloatingRates = LoanProductFloatingRates.builder()
            .interestRateDifferential(loanProductRequest.getInterestRateDifferential())
            .minDifferentialLendingRate(loanProductRequest.getMinDifferentialLendingRate())
            .maxDifferentialLendingRate(loanProductRequest.getMaxDifferentialLendingRate())
            .defaultDifferentialLendingRate(loanProductRequest.getDefaultDifferentialLendingRate())
            .isFloatingInterestRateCalculationAllowed(loanProductRequest.getIsFloatingInterestRateCalculationAllowed())
            .build();
        builder.floatingRates(loanProductFloatingRates);
      } else {
        interestFrequencyType = PeriodFrequencyType.fromInt(loanProductRequest.getInterestRateFrequencyType());
        interestRatePerPeriod = loanProductRequest.getInterestRatePerPeriod();
        minInterestRatePerPeriod = loanProductRequest.getMinInterestRatePerPeriod();
        maxInterestRatePerPeriod = loanProductRequest.getMaxInterestRatePerPeriod();

        annualInterestRate = aprCalculator.calculateFrom(interestFrequencyType, interestRatePerPeriod, numberOfRepayments,
            repaymentEvery, repaymentFrequencyType, daysInYearType);
//        loanProductRelatedDetailBuilder.annualNominalInterestRate(annualInterestRate);
      }

      final Boolean isVariableInstallmentsAllowed = loanProductRequest.getAllowVariableInstallments() != null ? loanProductRequest.getAllowVariableInstallments() : Boolean.FALSE;
      builder.allowVariabeInstallments(isVariableInstallmentsAllowed);

      if (isVariableInstallmentsAllowed) {
        minimumGapBetweenInstallments = loanProductRequest.getMinimumGapBetweenInstallments();
        maximumGapBetweenInstallments = loanProductRequest.getMaximumGapBetweenInstallments();
        LoanProductVariableInstallmentConfig loanProductVariableInstallmentConfig = LoanProductVariableInstallmentConfig.builder()
            .minimumGap(minimumGapBetweenInstallments)
            .maximumGap(maximumGapBetweenInstallments)
            .build();
        builder.variableInstallmentConfig(loanProductVariableInstallmentConfig);
      }

//      final BigDecimal inArrearsTolerance = loanProductRequest.getInArrearsTolerance();
//      loanProductRelatedDetailBuilder.inArrearsTolerance(inArrearsTolerance);
//
//      grace details
//      final Integer graceOnPrincipalPayment = loanProductRequest.getGraceOnPrincipalPayment();
//      final Integer recurringMoratoriumOnPrincipalPeriods = loanProductRequest.getRecurringMoratoriumOnPrincipalPeriods();
//      final Integer graceOnInterestPayment = loanProductRequest.getGraceOnInterestPayment();
//      final Integer graceOnInterestCharged = loanProductRequest.getGraceOnInterestCharged();

      final Integer minimumDaysBetweenDisbursalAndFirstRepayment = loanProductRequest.getMinimumDaysBetweenDisbursalAndFirstRepayment();
      builder.minimumDaysBetweenDisbursalAndFirstRepayment(minimumDaysBetweenDisbursalAndFirstRepayment);

//      loanProductRelatedDetailBuilder.graceOnPrincipalPayment(graceOnPrincipalPayment);
//      loanProductRelatedDetailBuilder.recurringMoratoriumOnPrincipalPeriods(recurringMoratoriumOnPrincipalPeriods);
//      loanProductRelatedDetailBuilder.graceOnInterestPayment(graceOnInterestPayment);
//      loanProductRelatedDetailBuilder.graceOnInterestCharged(graceOnInterestCharged);

      final AccountingRuleType accountingRuleType = AccountingRuleType.fromInt(loanProductRequest.getAccountingRule());
      builder.accountingRule(accountingRuleType);

      final Boolean includeInBorrowerCycle = loanProductRequest.getIncludeInBorrowerCycle();
      builder.includeInBorrowerCycle(includeInBorrowerCycle);

      final LocalDate startDate = loanProductRequest.getStartDate();

      final LocalDate closeDate = loanProductRequest.getCloseDate();

      final ExternalId externalId = ExternalIdFactory.produce(loanProductRequest.getExternalId());
      builder.startDate(startDate);
      builder.closeDate(closeDate);
      builder.externalId(externalId);

//      final LoanScheduleType loanScheduleType;
//      if (loanProductRequest.getLoanScheduleType() != null) {
//        loanScheduleType = LoanScheduleType.valueOf(loanProductRequest.getLoanScheduleType());
//        loanProductRelatedDetailBuilder.loanScheduleType(loanScheduleType);
//      } else {
//        // For backward compatibility
//        loanScheduleType = LoanScheduleType.CUMULATIVE;
//        loanProductRelatedDetailBuilder.loanScheduleType(loanScheduleType);
//      }
//
//      final LoanScheduleProcessingType loanScheduleProcessingType;
//      if (LoanScheduleType.PROGRESSIVE.equals(loanScheduleType) && loanProductRequest.getLoanScheduleProcessingType() != null) {
//        loanScheduleProcessingType = LoanScheduleProcessingType.valueOf(loanProductRequest.getLoanScheduleProcessingType());
//        loanProductRelatedDetailBuilder.loanScheduleProcessingType(loanScheduleProcessingType);
//      } else {
//        // For backward compatibility
//        loanScheduleProcessingType = LoanScheduleProcessingType.HORIZONTAL;
//        loanProductRelatedDetailBuilder.loanScheduleProcessingType(loanScheduleProcessingType);
//      }

      final Boolean useBorrowerCycle = loanProductRequest.getUseBorrowerCycle() != null ? loanProductRequest.getUseBorrowerCycle() : Boolean.FALSE;
      builder.useBorrowerCycle(useBorrowerCycle);

      if (useBorrowerCycle) {
        final Set<LoanProductBorrowerCycleVariations> loanProductBorrowerCycleVariations = populateBorrowerCycleVariations(loanProductRequest);
        builder.borrowerCycleVariations(loanProductBorrowerCycleVariations);
      }

      final Boolean multiDisburseLoan = loanProductRequest.getMultiDisburseLoan() != null ? loanProductRequest.getMultiDisburseLoan() : Boolean.FALSE;
      LoanProductTrancheDetails.LoanProductTrancheDetailsBuilder trancheDetailsBuilder = LoanProductTrancheDetails.builder();
      trancheDetailsBuilder.multiDisburseLoan(multiDisburseLoan);

      Integer maxTrancheCount = null;
      BigDecimal outstandingLoanBalance = null;

      if (multiDisburseLoan) {
        outstandingLoanBalance = loanProductRequest.getOutstandingLoanBalance();
        trancheDetailsBuilder.outstandingLoanBalance(outstandingLoanBalance);
        maxTrancheCount = loanProductRequest.getMaxTrancheCount();
        trancheDetailsBuilder.maxTrancheCount(maxTrancheCount);
      }
      builder.loanProductTrancheDetails(trancheDetailsBuilder.build());

//      final Integer graceOnArrearsAgeing = loanProductRequest.getGraceOnArrearsAgeing();
//      loanProductRelatedDetailBuilder.graceOnArrearsAgeing(graceOnArrearsAgeing);

      final Integer overdueDaysForNPA = loanProductRequest.getOverdueDaysForNPA();
      builder.overdueDaysForNPA(overdueDaysForNPA);

//      // Interest recalculation settings
      final Boolean isInterestRecalculationEnabled = loanProductRequest.getIsInterestRecalculationEnabled() != null ? loanProductRequest.getIsInterestRecalculationEnabled() : Boolean.FALSE;
//      loanProductRelatedDetailBuilder.daysInMonthType(loanProductRequest.getDaysInMonthType());
//
//      final DaysInYearCustomStrategyType daysInYearCustomStrategy = DaysInYearCustomStrategyType.fromString(loanProductRequest.getDaysInYearCustomStrategy());
//      loanProductRelatedDetailBuilder.daysInYearCustomStrategy(daysInYearCustomStrategy);

      LoanProductInterestRecalculationDetails interestRecalculationSettings = null;

      if (isInterestRecalculationEnabled) {
        interestRecalculationSettings = interestRecalculationDetailsAssembler.createFrom(loanProductRequest);
        builder.productInterestRecalculationDetails(interestRecalculationSettings);
      }

      final Boolean holdGuarantorFunds = loanProductRequest.getHoldGuaranteeFunds() != null ? loanProductRequest.getHoldGuaranteeFunds() : Boolean.FALSE;
      builder.holdGuaranteeFunds(holdGuarantorFunds);
      if (holdGuarantorFunds) {
        LoanProductGuaranteeDetails loanProductGuaranteeDetails = guaranteeDetailsAssembler.createFrom(loanProductRequest);
        builder.loanProductGuaranteeDetails(loanProductGuaranteeDetails);
      } else {
        builder.loanProductGuaranteeDetails(LoanProductGuaranteeDetails.builder()
                .mandatoryGuarantee(BigDecimal.ZERO)
                .minimumGuaranteeFromOwnFunds(BigDecimal.ZERO)
                .minimumGuaranteeFromGuarantor(BigDecimal.ZERO)
            .build());
      }

      LoanProductConfigurableAttributes loanConfigurableAttributes = null;
      if (loanProductRequest.getAllowAttributeOverrides() != null) {
        loanConfigurableAttributes = LoanProductConfigurableAttributes.createFrom(loanProductRequest);
        builder.loanConfigurableAttributes(loanConfigurableAttributes);
      } else {
        loanConfigurableAttributes = LoanProductConfigurableAttributes.populateDefaultsForConfigurableAttributes();
        builder.loanConfigurableAttributes(loanConfigurableAttributes);
      }

      BigDecimal principalThresholdForLastInstallment = loanProductRequest.getPrincipalThresholdForLastInstallment();

      if (principalThresholdForLastInstallment == null) {
        principalThresholdForLastInstallment = multiDisburseLoan
            ? LoanProductConstants.DEFAULT_PRINCIPAL_THRESHOLD_FOR_MULTI_DISBURSE_LOAN
            : LoanProductConstants.DEFAULT_PRINCIPAL_THRESHOLD_FOR_SINGLE_DISBURSE_LOAN;
      }
      builder.principalThresholdForLastInstallment(principalThresholdForLastInstallment);

      final Boolean accountMovesOutOfNPAOnlyOnArrearsCompletion = loanProductRequest.getAccountMovesOutOfNPAOnlyOnArrearsCompletion() != null ?
          loanProductRequest.getAccountMovesOutOfNPAOnlyOnArrearsCompletion() : Boolean.FALSE;
      builder.accountMovesOutOfNPAOnlyOnArrearsCompletion(accountMovesOutOfNPAOnlyOnArrearsCompletion);

      final Boolean canDefineEmiAmount = loanProductRequest.getCanDefineInstallmentAmount() != null ? loanProductRequest.getCanDefineInstallmentAmount() : Boolean.FALSE;
      builder.canDefineInstallmentAmount(canDefineEmiAmount);

//      final Integer installmentAmountInMultiplesOf = loanProductRequest.getInstallmentAmountInMultiplesOf();
//      loanProductRelatedDetailBuilder.installmentAmountInMultiplesOf(installmentAmountInMultiplesOf);

      final Boolean syncExpectedWithDisbursementDate = loanProductRequest.getSyncExpectedWithDisbursementDate() != null ? loanProductRequest.getSyncExpectedWithDisbursementDate() : Boolean.FALSE;
      builder.syncExpectedWithDisbursementDate(syncExpectedWithDisbursementDate);

      final Boolean canUseForTopup = loanProductRequest.getCanUseForTopup() != null ? loanProductRequest.getCanUseForTopup() : Boolean.FALSE;
      builder.canUseForTopup(canUseForTopup);

//      final Boolean isEqualAmortization = loanProductRequest.getIsEqualAmortization() != null ? loanProductRequest.getIsEqualAmortization() : Boolean.FALSE;
//      loanProductRelatedDetailBuilder.isEqualAmortization(isEqualAmortization);

      final BigDecimal fixedPrincipalPercentagePerInstallment = loanProductRequest.getFixedPrincipalPercentagePerInstallment();
      builder.fixedPrincipalPercentagePerInstallment(fixedPrincipalPercentagePerInstallment);

      final Boolean disallowExpectedDisbursements = loanProductRequest.getDisallowExpectedDisbursements() != null ? loanProductRequest.getDisallowExpectedDisbursements() : Boolean.FALSE;
      builder.disallowExpectedDisbursements(disallowExpectedDisbursements);

      final Boolean allowApprovedDisbursedAmountsOverApplied = loanProductRequest.getAllowApprovedDisbursedAmountsOverApplied() != null ?
          loanProductRequest.getAllowApprovedDisbursedAmountsOverApplied() : Boolean.FALSE;
      builder.allowApprovedDisbursedAmountsOverApplied(allowApprovedDisbursedAmountsOverApplied);

      final String overAppliedCalculationType = loanProductRequest.getOverAppliedCalculationType();
      builder.overAppliedCalculationType(overAppliedCalculationType);

      final Integer overAppliedNumber = loanProductRequest.getOverAppliedNumber();
      builder.overAppliedNumber(overAppliedNumber);

      final Integer dueDaysForRepaymentEvent = loanProductRequest.getDueDaysForRepaymentEvent();
      builder.dueDaysForRepaymentEvent(dueDaysForRepaymentEvent);

      final Integer overDueDaysForRepaymentEvent = loanProductRequest.getOverDueDaysForRepaymentEvent();
      builder.overDueDaysForRepaymentEvent(overDueDaysForRepaymentEvent);

//      final Boolean enableDownPayment = loanProductRequest.getEnableDownPayment() != null ? loanProductRequest.getEnableDownPayment() : Boolean.FALSE;
//      loanProductRelatedDetailBuilder.enableDownPayment(enableDownPayment);
//
//      final BigDecimal disbursedAmountPercentageDownPayment = loanProductRequest.getDisbursedAmountPercentageForDownPayment();
//      loanProductRelatedDetailBuilder.disbursedAmountPercentageForDownPayment(disbursedAmountPercentageDownPayment);
//
//      final Boolean enableAutoRepaymentForDownPayment = loanProductRequest.getEnableAutoRepaymentForDownPayment() != null ? loanProductRequest.getEnableAutoRepaymentForDownPayment() : Boolean.FALSE;
//      loanProductRelatedDetailBuilder.enableAutoRepaymentForDownPayment(enableAutoRepaymentForDownPayment);

      final RepaymentStartDateType repaymentStartDateType = RepaymentStartDateType
          .fromInt(loanProductRequest.getRepaymentStartDateType());
      builder.repaymentStartDateType(repaymentStartDateType);

      final Boolean enableInstallmentLevelDelinquency = loanProductRequest.getEnableInstallmentLevelDelinquency() != null ? loanProductRequest.getEnableInstallmentLevelDelinquency() : Boolean.FALSE;
      builder.enableInstallmentLevelDelinquency(enableInstallmentLevelDelinquency);

//      final Integer fixedLength = loanProductRequest.getFixedLength();
//      loanProductRelatedDetailBuilder.fixedLength(fixedLength);
//
//      final Boolean enableAccrualActivityPosting = loanProductRequest.getEnableAccrualActivityPosting() != null ? loanProductRequest.getEnableAccrualActivityPosting() : Boolean.FALSE;
//      loanProductRelatedDetailBuilder.enableAccrualActivityPosting(enableAccrualActivityPosting);
//
//      Boolean interestRecognitionOnDisbursementDate = loanProductRequest.getInterestRecognitionOnDisbursementDate() != null ? loanProductRequest.getInterestRecognitionOnDisbursementDate() : Boolean.FALSE;
//      loanProductRelatedDetailBuilder.interestRecognitionOnDisbursementDate(interestRecognitionOnDisbursementDate);
//      List<LoanSupportedInterestRefundTypes> supportedInterestRefundTypes = new ArrayList<>();
//      if (loanProductRequest.getSupportedInterestRefundTypes() != null && !loanProductRequest.getSupportedInterestRefundTypes().isEmpty()) {
//        for(String element : loanProductRequest.getSupportedInterestRefundTypes()) {
//          supportedInterestRefundTypes.add(LoanSupportedInterestRefundTypes.valueOf(element));
//        }
//        loanProductRelatedDetailBuilder.supportedInterestRefundTypes(supportedInterestRefundTypes);
//      } else {
//        loanProductRelatedDetailBuilder.supportedInterestRefundTypes(List.of(LoanSupportedInterestRefundTypes.PAYOUT_REFUND));
//      }
//
//      final LoanChargeOffBehaviour chargeOffBehaviour;
//      if (loanProductRequest.getChargeOffBehaviour() != null) {
//        chargeOffBehaviour = LoanChargeOffBehaviour.valueOf(loanProductRequest.getChargeOffBehaviour());
//        loanProductRelatedDetailBuilder.chargeOffBehaviour(chargeOffBehaviour);
//      } else {
//        // For backward compatibility
//        chargeOffBehaviour = LoanChargeOffBehaviour.REGULAR;
//        loanProductRelatedDetailBuilder.chargeOffBehaviour(chargeOffBehaviour);
//      }
//
//      final Boolean enableIncomeCapitalization = loanProductRequest.getEnableIncomeCapitalization() != null ? loanProductRequest.getEnableIncomeCapitalization() : Boolean.FALSE;
//      loanProductRelatedDetailBuilder.enableIncomeCapitalization(enableIncomeCapitalization);
//
//      final LoanCapitalizedIncomeCalculationType capitalizedIncomeCalculationType = loanProductRequest.getCapitalizedIncomeCalculationType() != null ?
//          LoanCapitalizedIncomeCalculationType.valueOf(loanProductRequest.getCapitalizedIncomeCalculationType()) : LoanCapitalizedIncomeCalculationType.FLAT;
//      loanProductRelatedDetailBuilder.capitalizedIncomeCalculationType(capitalizedIncomeCalculationType);
//
//      final LoanCapitalizedIncomeStrategy capitalizedIncomeStrategy = loanProductRequest.getCapitalizedIncomeStrategy() != null ?
//          LoanCapitalizedIncomeStrategy.valueOf(loanProductRequest.getCapitalizedIncomeStrategy()) : LoanCapitalizedIncomeStrategy.EQUAL_AMORTIZATION;
//      loanProductRelatedDetailBuilder.capitalizedIncomeStrategy(capitalizedIncomeStrategy);
//
//      final LoanCapitalizedIncomeType capitalizedIncomeType = loanProductRequest.getCapitalizedIncomeType() != null ? LoanCapitalizedIncomeType.valueOf(loanProductRequest.getCapitalizedIncomeType()) : LoanCapitalizedIncomeType.INTEREST;
//      loanProductRelatedDetailBuilder.capitalizedIncomeType(capitalizedIncomeType);
//
//      final Boolean enableBuyDownFee = loanProductRequest.getEnableBuyDownFee() != null ? loanProductRequest.getMerchantBuyDownFee() : Boolean.FALSE;
//      loanProductRelatedDetailBuilder.enableBuyDownFee(enableBuyDownFee);
//
//      final LoanBuyDownFeeCalculationType buyDownFeeCalculationType = loanProductRequest.getBuyDownFeeCalculationType() != null ? LoanBuyDownFeeCalculationType.valueOf(loanProductRequest.getBuyDownFeeCalculationType()) : LoanBuyDownFeeCalculationType.FLAT;
//      loanProductRelatedDetailBuilder.buyDownFeeCalculationType(buyDownFeeCalculationType);
//
//      final LoanBuyDownFeeStrategy buyDownFeeStrategy = loanProductRequest.getBuyDownFeeStrategy() != null ?
//          LoanBuyDownFeeStrategy.valueOf(loanProductRequest.getBuyDownFeeStrategy()) : LoanBuyDownFeeStrategy.EQUAL_AMORTIZATION;
//      loanProductRelatedDetailBuilder.buyDownFeeStrategy(buyDownFeeStrategy);
//
//      final LoanBuyDownFeeIncomeType buyDownFeeIncomeType = loanProductRequest.getBuyDownFeeIncomeType() != null ? LoanBuyDownFeeIncomeType.valueOf(loanProductRequest.getBuyDownFeeIncomeType()) : LoanBuyDownFeeIncomeType.INTEREST;
//      loanProductRelatedDetailBuilder.buyDownFeeIncomeType(buyDownFeeIncomeType);
//
//      final Boolean merchantBuyDownFee = loanProductRequest.getMerchantBuyDownFee() != null ? loanProductRequest.getMerchantBuyDownFee() : Boolean.FALSE;
//      loanProductRelatedDetailBuilder.merchantBuyDownFee(merchantBuyDownFee);
//      builder.loanProductRelatedDetail(loanProductRelatedDetailBuilder.build());
      LoanProductRelatedDetail loanProductRelatedDetail = loanProductRelatedDetail(loanProductRequest);
      loanProductRelatedDetail.setAnnualNominalInterestRate(annualInterestRate);
      builder.loanProductRelatedDetail(loanProductRelatedDetail(loanProductRequest));

      LoanProduct loanProduct = builder.build();
      loanProduct.getLoanConfigurableAttributes().setLoanProduct(loanProduct);
      loanProduct.getLoanProductGuaranteeDetails().setLoanProduct(loanProduct);
      return loanProduct;
    }

    private LoanProductRelatedDetail loanProductRelatedDetail(LoanProductRequest loanProductRequest) {
      final LoanProductRelatedDetail.LoanProductRelatedDetailBuilder loanProductRelatedDetailBuilder = LoanProductRelatedDetail.builder();

      final MonetaryCurrency currency = MonetaryCurrency.builder()
          .code(loanProductRequest.getCurrencyCode())
          .digitsAfterDecimal(loanProductRequest.getDigitsAfterDecimal())
          .inMultiplesOf(loanProductRequest.getInMultiplesOf())
          .build();
      loanProductRelatedDetailBuilder.currency(currency);
      loanProductRelatedDetailBuilder.principal(loanProductRequest.getPrincipal());

      final InterestMethod interestMethod = InterestMethod.fromInt(loanProductRequest.getInterestType());
      loanProductRelatedDetailBuilder.interestMethod(interestMethod);

      final InterestCalculationPeriodMethod interestCalculationPeriodMethod = InterestCalculationPeriodMethod
          .fromInt(loanProductRequest.getInterestCalculationPeriod() == 0 ? 1 : loanProductRequest.getInterestCalculationPeriod());
      loanProductRelatedDetailBuilder.interestCalculationPeriodMethod(interestCalculationPeriodMethod);

      final Boolean allowPartialPeriodInterestCalculation = loanProductRequest.getAllowPartialPeriodInterestCalculation() != null ? loanProductRequest.getAllowPartialPeriodInterestCalculation() : Boolean.FALSE;
      loanProductRelatedDetailBuilder.allowPartialPeriodInterestCalculation(allowPartialPeriodInterestCalculation);

      final AmortizationMethod amortizationMethod = AmortizationMethod.fromInt(loanProductRequest.getAmortizationType());
      loanProductRelatedDetailBuilder.amortizationMethod(amortizationMethod);

      final PeriodFrequencyType repaymentFrequencyType = PeriodFrequencyType
          .fromInt(loanProductRequest.getRepaymentFrequencyType());
      loanProductRelatedDetailBuilder.repaymentPeriodFrequencyType(repaymentFrequencyType);

      PeriodFrequencyType interestFrequencyType = PeriodFrequencyType.INVALID;
      loanProductRelatedDetailBuilder.interestPeriodFrequencyType(interestFrequencyType);

      final DaysInYearType daysInYearType = DaysInYearType.fromInt(loanProductRequest.getDaysInYearType());
      loanProductRelatedDetailBuilder.daysInYearType(loanProductRequest.getDaysInYearType());

      final Integer repaymentEvery = loanProductRequest.getRepaymentEvery() == null ? 1 : loanProductRequest.getRepaymentEvery();
      loanProductRelatedDetailBuilder.repayEvery(repaymentEvery);

      final Integer numberOfRepayments = loanProductRequest.getNumberOfRepayments() == null ? 0 : loanProductRequest.getNumberOfRepayments();
      loanProductRelatedDetailBuilder.numberOfRepayments(numberOfRepayments);

      final BigDecimal inArrearsTolerance = loanProductRequest.getInArrearsTolerance();
      loanProductRelatedDetailBuilder.inArrearsTolerance(inArrearsTolerance);

      final Integer graceOnPrincipalPayment = loanProductRequest.getGraceOnPrincipalPayment();
      final Integer recurringMoratoriumOnPrincipalPeriods = loanProductRequest.getRecurringMoratoriumOnPrincipalPeriods();
      final Integer graceOnInterestPayment = loanProductRequest.getGraceOnInterestPayment();
      final Integer graceOnInterestCharged = loanProductRequest.getGraceOnInterestCharged();

      loanProductRelatedDetailBuilder.graceOnPrincipalPayment(graceOnPrincipalPayment);
      loanProductRelatedDetailBuilder.recurringMoratoriumOnPrincipalPeriods(recurringMoratoriumOnPrincipalPeriods);
      loanProductRelatedDetailBuilder.graceOnInterestPayment(graceOnInterestPayment);
      loanProductRelatedDetailBuilder.graceOnInterestCharged(graceOnInterestCharged);

      final LoanScheduleType loanScheduleType;
      if (loanProductRequest.getLoanScheduleType() != null) {
        loanScheduleType = LoanScheduleType.valueOf(loanProductRequest.getLoanScheduleType());
        loanProductRelatedDetailBuilder.loanScheduleType(loanScheduleType);
      } else {
        // For backward compatibility
        loanScheduleType = LoanScheduleType.CUMULATIVE;
        loanProductRelatedDetailBuilder.loanScheduleType(loanScheduleType);
      }

      final LoanScheduleProcessingType loanScheduleProcessingType;
      if (LoanScheduleType.PROGRESSIVE.equals(loanScheduleType) && loanProductRequest.getLoanScheduleProcessingType() != null) {
        loanScheduleProcessingType = LoanScheduleProcessingType.valueOf(loanProductRequest.getLoanScheduleProcessingType());
        loanProductRelatedDetailBuilder.loanScheduleProcessingType(loanScheduleProcessingType);
      } else {
        // For backward compatibility
        loanScheduleProcessingType = LoanScheduleProcessingType.HORIZONTAL;
        loanProductRelatedDetailBuilder.loanScheduleProcessingType(loanScheduleProcessingType);
      }

      final Integer graceOnArrearsAgeing = loanProductRequest.getGraceOnArrearsAgeing();
      loanProductRelatedDetailBuilder.graceOnArrearsAgeing(graceOnArrearsAgeing);

      // Interest recalculation settings
      final Boolean isInterestRecalculationEnabled = loanProductRequest.getIsInterestRecalculationEnabled() != null ? loanProductRequest.getIsInterestRecalculationEnabled() : Boolean.FALSE;
      loanProductRelatedDetailBuilder.daysInMonthType(loanProductRequest.getDaysInMonthType());

      final DaysInYearCustomStrategyType daysInYearCustomStrategy = DaysInYearCustomStrategyType.fromString(loanProductRequest.getDaysInYearCustomStrategy());
      loanProductRelatedDetailBuilder.daysInYearCustomStrategy(daysInYearCustomStrategy);

      final Integer installmentAmountInMultiplesOf = loanProductRequest.getInstallmentAmountInMultiplesOf();
      loanProductRelatedDetailBuilder.installmentAmountInMultiplesOf(installmentAmountInMultiplesOf);

      final Boolean isEqualAmortization = loanProductRequest.getIsEqualAmortization() != null ? loanProductRequest.getIsEqualAmortization() : Boolean.FALSE;
      loanProductRelatedDetailBuilder.isEqualAmortization(isEqualAmortization);

      final Boolean enableDownPayment = loanProductRequest.getEnableDownPayment() != null ? loanProductRequest.getEnableDownPayment() : Boolean.FALSE;
      loanProductRelatedDetailBuilder.enableDownPayment(enableDownPayment);

      final BigDecimal disbursedAmountPercentageDownPayment = loanProductRequest.getDisbursedAmountPercentageForDownPayment();
      loanProductRelatedDetailBuilder.disbursedAmountPercentageForDownPayment(disbursedAmountPercentageDownPayment);

      final Boolean enableAutoRepaymentForDownPayment = loanProductRequest.getEnableAutoRepaymentForDownPayment() != null ? loanProductRequest.getEnableAutoRepaymentForDownPayment() : Boolean.FALSE;
      loanProductRelatedDetailBuilder.enableAutoRepaymentForDownPayment(enableAutoRepaymentForDownPayment);

      final Integer fixedLength = loanProductRequest.getFixedLength();
      loanProductRelatedDetailBuilder.fixedLength(fixedLength);

      final Boolean enableAccrualActivityPosting = loanProductRequest.getEnableAccrualActivityPosting() != null ? loanProductRequest.getEnableAccrualActivityPosting() : Boolean.FALSE;
      loanProductRelatedDetailBuilder.enableAccrualActivityPosting(enableAccrualActivityPosting);

      Boolean interestRecognitionOnDisbursementDate = loanProductRequest.getInterestRecognitionOnDisbursementDate() != null ? loanProductRequest.getInterestRecognitionOnDisbursementDate() : Boolean.FALSE;
      loanProductRelatedDetailBuilder.interestRecognitionOnDisbursementDate(interestRecognitionOnDisbursementDate);
      List<LoanSupportedInterestRefundTypes> supportedInterestRefundTypes = new ArrayList<>();
      if (loanProductRequest.getSupportedInterestRefundTypes() != null && !loanProductRequest.getSupportedInterestRefundTypes().isEmpty()) {
        for(String element : loanProductRequest.getSupportedInterestRefundTypes()) {
          supportedInterestRefundTypes.add(LoanSupportedInterestRefundTypes.valueOf(element));
        }
        loanProductRelatedDetailBuilder.supportedInterestRefundTypes(supportedInterestRefundTypes);
      } else {
        loanProductRelatedDetailBuilder.supportedInterestRefundTypes(List.of(LoanSupportedInterestRefundTypes.PAYOUT_REFUND));
      }

      final LoanChargeOffBehaviour chargeOffBehaviour;
      if (loanProductRequest.getChargeOffBehaviour() != null) {
        chargeOffBehaviour = LoanChargeOffBehaviour.valueOf(loanProductRequest.getChargeOffBehaviour());
        loanProductRelatedDetailBuilder.chargeOffBehaviour(chargeOffBehaviour);
      } else {
        // For backward compatibility
        chargeOffBehaviour = LoanChargeOffBehaviour.REGULAR;
        loanProductRelatedDetailBuilder.chargeOffBehaviour(chargeOffBehaviour);
      }

      final Boolean enableIncomeCapitalization = loanProductRequest.getEnableIncomeCapitalization() != null ? loanProductRequest.getEnableIncomeCapitalization() : Boolean.FALSE;
      loanProductRelatedDetailBuilder.enableIncomeCapitalization(enableIncomeCapitalization);

      final LoanCapitalizedIncomeCalculationType capitalizedIncomeCalculationType = loanProductRequest.getCapitalizedIncomeCalculationType() != null ?
          LoanCapitalizedIncomeCalculationType.valueOf(loanProductRequest.getCapitalizedIncomeCalculationType()) : LoanCapitalizedIncomeCalculationType.FLAT;
      loanProductRelatedDetailBuilder.capitalizedIncomeCalculationType(capitalizedIncomeCalculationType);

      final LoanCapitalizedIncomeStrategy capitalizedIncomeStrategy = loanProductRequest.getCapitalizedIncomeStrategy() != null ?
          LoanCapitalizedIncomeStrategy.valueOf(loanProductRequest.getCapitalizedIncomeStrategy()) : LoanCapitalizedIncomeStrategy.EQUAL_AMORTIZATION;
      loanProductRelatedDetailBuilder.capitalizedIncomeStrategy(capitalizedIncomeStrategy);

      final LoanCapitalizedIncomeType capitalizedIncomeType = loanProductRequest.getCapitalizedIncomeType() != null ? LoanCapitalizedIncomeType.valueOf(loanProductRequest.getCapitalizedIncomeType()) : LoanCapitalizedIncomeType.INTEREST;
      loanProductRelatedDetailBuilder.capitalizedIncomeType(capitalizedIncomeType);

      final Boolean enableBuyDownFee = loanProductRequest.getEnableBuyDownFee() != null ? loanProductRequest.getMerchantBuyDownFee() : Boolean.FALSE;
      loanProductRelatedDetailBuilder.enableBuyDownFee(enableBuyDownFee);

      final LoanBuyDownFeeCalculationType buyDownFeeCalculationType = loanProductRequest.getBuyDownFeeCalculationType() != null ? LoanBuyDownFeeCalculationType.valueOf(loanProductRequest.getBuyDownFeeCalculationType()) : LoanBuyDownFeeCalculationType.FLAT;
      loanProductRelatedDetailBuilder.buyDownFeeCalculationType(buyDownFeeCalculationType);

      final LoanBuyDownFeeStrategy buyDownFeeStrategy = loanProductRequest.getBuyDownFeeStrategy() != null ?
          LoanBuyDownFeeStrategy.valueOf(loanProductRequest.getBuyDownFeeStrategy()) : LoanBuyDownFeeStrategy.EQUAL_AMORTIZATION;
      loanProductRelatedDetailBuilder.buyDownFeeStrategy(buyDownFeeStrategy);

      final LoanBuyDownFeeIncomeType buyDownFeeIncomeType = loanProductRequest.getBuyDownFeeIncomeType() != null ? LoanBuyDownFeeIncomeType.valueOf(loanProductRequest.getBuyDownFeeIncomeType()) : LoanBuyDownFeeIncomeType.INTEREST;
      loanProductRelatedDetailBuilder.buyDownFeeIncomeType(buyDownFeeIncomeType);

      final Boolean merchantBuyDownFee = loanProductRequest.getMerchantBuyDownFee() != null ? loanProductRequest.getMerchantBuyDownFee() : Boolean.FALSE;
      loanProductRelatedDetailBuilder.merchantBuyDownFee(merchantBuyDownFee);

      return loanProductRelatedDetailBuilder.build();
    }

    private LoanProductMinMaxConstraints loanProductMinMaxConstraints(LoanProductRequest loanProductRequest) {
      return LoanProductMinMaxConstraints.builder()
          .minPrincipal(loanProductRequest.getMinPrincipal())
          .maxPrincipal(loanProductRequest.getMaxPrincipal())
          .minNominalInterestRatePerPeriod(loanProductRequest.getMinNominalInterestRatePerPeriod())
          .maxNominalInterestRatePerPeriod(loanProductRequest.getMaxNominalInterestRatePerPeriod())
          .minNumberOfRepayments(loanProductRequest.getMinNumberOfRepayments())
          .maxNumberOfRepayments(loanProductRequest.getMaxNumberOfRepayments())
          .build();
    }

    private Set<LoanProductBorrowerCycleVariations> populateBorrowerCycleVariations(LoanProductRequest loanProductRequest) {
      Set<LoanProductBorrowerCycleVariationsDTO> principalVariations = loanProductRequest.getPrincipalVariationsForBorrowerCycle();
      Set<LoanProductBorrowerCycleVariationsDTO> repaymentVariations = loanProductRequest.getNumberOfRepaymentVariationsForBorrowerCycle();
      Set<LoanProductBorrowerCycleVariationsDTO> interestRateVariations = loanProductRequest.getInterestRateVariationsForBorrowerCycle();

      Set<LoanProductBorrowerCycleVariations> response = new HashSet<>();
      response.addAll(loanProductBorrowerCycleVariationsToLoanProductBorrowCycleVariationsDTOMapper.toLoanProductBorrowCycleVariationSet(principalVariations));
      response.addAll(loanProductBorrowerCycleVariationsToLoanProductBorrowCycleVariationsDTOMapper.toLoanProductBorrowCycleVariationSet(repaymentVariations));
      response.addAll(loanProductBorrowerCycleVariationsToLoanProductBorrowCycleVariationsDTOMapper.toLoanProductBorrowCycleVariationSet(interestRateVariations));

      return response;
    }

    private void populateBorrowerCycleVariations(final JsonCommand command,
            final Set<LoanProductBorrowerCycleVariations> loanProductBorrowerCycleVariations) {
        assemblePrincipalVariations(command, loanProductBorrowerCycleVariations);
        assembleRepaymentVariations(command, loanProductBorrowerCycleVariations);
        assembleInterestRateVariations(command, loanProductBorrowerCycleVariations);
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
}
