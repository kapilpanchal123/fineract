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
package org.apache.fineract.portfolio.loanproduct.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.accounting.common.AccountingEnumerations;
import org.apache.fineract.infrastructure.core.api.ApiFacingEnum;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.data.StringEnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.database.DatabaseSpecificSQLGenerator;
import org.apache.fineract.infrastructure.entityaccess.domain.FineractEntityType;
import org.apache.fineract.infrastructure.entityaccess.service.FineractEntityAccessUtil;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.service.ChargeReadPlatformService;
import org.apache.fineract.portfolio.common.domain.DaysInYearCustomStrategyType;
import org.apache.fineract.portfolio.common.service.CommonEnumerations;
import org.apache.fineract.portfolio.delinquency.data.DelinquencyBucketData;
import org.apache.fineract.portfolio.delinquency.service.DelinquencyReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.domain.LoanBuyDownFeeCalculationType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanBuyDownFeeIncomeType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanBuyDownFeeStrategy;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCapitalizedIncomeCalculationType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCapitalizedIncomeStrategy;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCapitalizedIncomeType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanChargeOffBehaviour;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleProcessingType;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleType;
import org.apache.fineract.portfolio.loanproduct.data.AdvancedPaymentData;
import org.apache.fineract.portfolio.loanproduct.data.CreditAllocationData;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductBorrowerCycleVariationData;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductGuaranteeData;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductInterestRecalculationData;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductConfigurableAttributes;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductParamType;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductRepository;
import org.apache.fineract.portfolio.loanproduct.domain.LoanSupportedInterestRefundTypes;
import org.apache.fineract.portfolio.loanproduct.exception.LoanProductNotFoundException;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.apache.fineract.portfolio.rate.data.RateData;
import org.apache.fineract.portfolio.rate.service.RateReadService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class LoanProductReadPlatformRepository {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final ChargeReadPlatformService chargeReadPlatformService;
    private final RateReadService rateReadService;
    private final DatabaseSpecificSQLGenerator sqlGenerator;
    private final FineractEntityAccessUtil fineractEntityAccessUtil;
    private final DelinquencyReadPlatformService delinquencyReadPlatformService;
    private final LoanProductRepository loanProductRepository;
    private final List<ChargeData> charges;
    private final List<LoanProductBorrowerCycleVariationData> borrowerCycleVariationDatas;
    private final List<AdvancedPaymentData> advancedPaymentData;
    private final List<CreditAllocationData> creditAllocationData;
    private final List<RateData> rates;
    private final List<DelinquencyBucketData> delinquencyBucketOptions;

    public List<LoanProductData> retrieveAllLoanProducts() {
        String sql = "SELECT " + loanProductSchema();

        // Check if branch specific products are enabled. If yes, fetch only
        // products mapped to current user's office
        String inClause = fineractEntityAccessUtil
                .getSQLWhereClauseForProductIDsForUserOffice_ifGlobalConfigEnabled(FineractEntityType.LOAN_PRODUCT);
        if (inClause != null && !inClause.trim().isEmpty()) {
            sql += " WHERE lp.id IN ( " + inClause + " ) ";
        }
        return this.jdbcTemplate.query(sql, loanProductMapper(null, null, null, null, null, null));
    }

    public LoanProductData retrieveLoanProduct(final Long loanProductId) {
        try {
            final List<ChargeData> charges = this.chargeReadPlatformService.retrieveLoanProductCharges(loanProductId);
            final List<RateData> rates = this.rateReadService.retrieveProductLoanRates(loanProductId);
            final List<LoanProductBorrowerCycleVariationData> borrowerCycleVariationDatas = retrieveLoanProductBorrowerCycleVariations(
                    loanProductId);
            final List<AdvancedPaymentData> advancedPaymentData = retrieveAdvancedPaymentData(loanProductId);
            final List<CreditAllocationData> creditAllocationData = retrieveCreditAllocationData(loanProductId);
            final List<DelinquencyBucketData> delinquencyBucketOptions = this.delinquencyReadPlatformService
                    .retrieveAllDelinquencyBuckets();
            final String sql = "SELECT " + loanProductSchema() + " WHERE lp.id = ?";

            return this.jdbcTemplate.queryForObject(sql, loanProductMapper(charges, borrowerCycleVariationDatas, rates,
                    delinquencyBucketOptions, advancedPaymentData, creditAllocationData), loanProductId);
        } catch (final EmptyResultDataAccessException e) {
            throw new LoanProductNotFoundException(loanProductId, e);
        }
    }

    private List<CreditAllocationData> retrieveCreditAllocationData(Long loanProductId) {
        final String sql = "SELECT " + creditAllocationDataSchema() + " WHERE loan_product_id = ?";
        return this.jdbcTemplate.query(sql, creditAllocationDataMapper(), loanProductId);
    }

    private RowMapper<CreditAllocationData> creditAllocationDataMapper() {
        return (rs, rowNum) -> {
            final String transactionType = rs.getString("transaction_type");
            final String allocationTypes = rs.getString("allocation_types");
            return new CreditAllocationData(transactionType, creditAllocationDataConvert(allocationTypes));
        };
    }

    private List<CreditAllocationData.CreditAllocationOrder> creditAllocationDataConvert(String allocationOrders) {
        String[] allocationRule = allocationOrders.split(",");
        AtomicInteger order = new AtomicInteger(1);
        return Arrays.stream(allocationRule).map(s -> new CreditAllocationData.CreditAllocationOrder(s, order.getAndIncrement())).toList();
    }

    private String creditAllocationDataSchema() {
        return "transaction_type, allocation_types from m_loan_product_credit_allocation_rule";
    }

    private List<AdvancedPaymentData> retrieveAdvancedPaymentData(Long loanProductId) {
        final String sql = "SELECT " + advancedPaymentDataSchema() + " WHERE loan_product_id = ?";
        return this.jdbcTemplate.query(sql, advancedPaymentDataMapper(), loanProductId);
    }

    private RowMapper<AdvancedPaymentData> advancedPaymentDataMapper() {
        return (rs, rowNum) -> {
            final String transactionType = rs.getString("transaction_type");
            final String allocationTypes = rs.getString("allocation_types");
            final String futureInstallmentAllocationRule = rs.getString("future_installment_allocation_rule");
            return new AdvancedPaymentData(transactionType, futureInstallmentAllocationRule, advancedPaymentDataConvert(allocationTypes));
        };
    }

    private List<AdvancedPaymentData.PaymentAllocationOrder> advancedPaymentDataConvert(String allocationOrders) {
        String[] allocationRule = allocationOrders.split(",");
        AtomicInteger order = new AtomicInteger(1);
        return Arrays.stream(allocationRule).map(s -> new AdvancedPaymentData.PaymentAllocationOrder(s, order.getAndIncrement())).toList();
    }

    private String advancedPaymentDataSchema() {
        return "transaction_type, allocation_types, future_installment_allocation_rule from m_loan_product_payment_allocation_rule";
    }

    private List<LoanProductBorrowerCycleVariationData> retrieveLoanProductBorrowerCycleVariations(Long loanProductId) {
        final String sql = "SELECT " + loanProductBorrowerCycleSchema()
                + " WHERE bc.loan_product_id=? ORDER BY bc.borrower_cycle_number, bc.value_condition";
        return this.jdbcTemplate.query(sql, loanProductBorrowerCycleMapper(), loanProductId);
    }

    private RowMapper<LoanProductBorrowerCycleVariationData> loanProductBorrowerCycleMapper() {
        return (rs, rowNum) -> {
            final Long id = rs.getLong("id");
            final Integer cycleNumber = JdbcSupport.getInteger(rs, "cycleNumber");
            final Integer conditionType = JdbcSupport.getInteger(rs, "conditionType");
            final EnumOptionData conditionTypeData = LoanEnumerations.loanCycleValueConditionType(conditionType);
            final Integer paramType = JdbcSupport.getInteger(rs, "paramType");
            final EnumOptionData paramTypeData = LoanEnumerations.loanCycleParamType(paramType);
            final BigDecimal defaultValue = rs.getBigDecimal("defaultValue");
            final BigDecimal maxValue = rs.getBigDecimal("maxVal");
            final BigDecimal minValue = rs.getBigDecimal("minVal");
            return new LoanProductBorrowerCycleVariationData(id, cycleNumber, paramTypeData, conditionTypeData, defaultValue, minValue,
                    maxValue);
        };
    }

    private String loanProductBorrowerCycleSchema() {
        return "bc.id as id,bc.borrower_cycle_number as cycleNumber,bc.value_condition as conditionType,bc.param_type as paramType,"
                + "bc.default_value as defaultValue,bc.max_value as maxVal,bc.min_value as minVal "
                + "from m_product_loan_variations_borrower_cycle bc";
    }

    private String loanProductSchema() {
        return "lp.id as id, lp.fund_id as fundId, f.name as fundName, lp.loan_transaction_strategy_code as transactionStrategyCode, lp.loan_transaction_strategy_name as transactionStrategyName, "
                + "lp.name as name, lp.short_name as shortName, lp.description as description, "
                + "lp.principal_amount as principal, lp.min_principal_amount as minPrincipal, lp.max_principal_amount as maxPrincipal, lp.currency_code as currencyCode, lp.currency_digits as currencyDigits, lp.currency_multiplesof as inMultiplesOf, "
                + "lp.nominal_interest_rate_per_period as interestRatePerPeriod, lp.min_nominal_interest_rate_per_period as minInterestRatePerPeriod, lp.max_nominal_interest_rate_per_period as maxInterestRatePerPeriod, lp.interest_period_frequency_enum as interestRatePerPeriodFreq, "
                + "lp.annual_nominal_interest_rate as annualInterestRate, lp.interest_method_enum as interestMethod, lp.interest_calculated_in_period_enum as interestCalculationInPeriodMethod,lp.allow_partial_period_interest_calcualtion as allowPartialPeriodInterestCalcualtion, "
                + "lp.repay_every as repaidEvery, lp.repayment_period_frequency_enum as repaymentPeriodFrequency, lp.number_of_repayments as numberOfRepayments, lp.min_number_of_repayments as minNumberOfRepayments, lp.max_number_of_repayments as maxNumberOfRepayments, "
                + "lp.fixed_length as fixedLength, lp.enable_accrual_activity_posting as enableAccrualActivityPosting, "
                + "lp.grace_on_principal_periods as graceOnPrincipalPayment, lp.recurring_moratorium_principal_periods as recurringMoratoriumOnPrincipalPeriods, lp.grace_on_interest_periods as graceOnInterestPayment, lp.grace_interest_free_periods as graceOnInterestCharged,lp.grace_on_arrears_ageing as graceOnArrearsAgeing,lp.overdue_days_for_npa as overdueDaysForNPA, "
                + "lp.min_days_between_disbursal_and_first_repayment As minimumDaysBetweenDisbursalAndFirstRepayment, "
                + "lp.amortization_method_enum as amortizationMethod, lp.arrearstolerance_amount as tolerance, "
                + "lp.accounting_type as accountingType, lp.include_in_borrower_cycle as includeInBorrowerCycle,lp.use_borrower_cycle as useBorrowerCycle, lp.start_date as startDate, lp.close_date as closeDate,  "
                + "lp.allow_multiple_disbursals as multiDisburseLoan, lp.max_disbursals as maxTrancheCount, lp.max_outstanding_loan_balance as outstandingLoanBalance, "
                + "lp.disallow_expected_disbursements as disallowExpectedDisbursements, lp.allow_approved_disbursed_amounts_over_applied as allowApprovedDisbursedAmountsOverApplied, lp.over_applied_calculation_type as overAppliedCalculationType, over_applied_number as overAppliedNumber, "
                + "lp.days_in_month_enum as daysInMonth, lp.days_in_year_enum as daysInYear, lp.interest_recalculation_enabled as isInterestRecalculationEnabled, "
                + "lp.can_define_fixed_emi_amount as canDefineInstallmentAmount, lp.installment_amount_in_multiples_of as installmentAmountInMultiplesOf, "
                + "lp.due_days_for_repayment_event as dueDaysForRepaymentEvent, lp.overdue_days_for_repayment_event as overDueDaysForRepaymentEvent, lp.enable_down_payment as enableDownPayment, lp.disbursed_amount_percentage_for_down_payment as disbursedAmountPercentageForDownPayment, lp.enable_auto_repayment_for_down_payment as enableAutoRepaymentForDownPayment, lp.repayment_start_date_type_enum as repaymentStartDateType, "
                + "lp.enable_installment_level_delinquency as enableInstallmentLevelDelinquency, "
                + "lpr.pre_close_interest_calculation_strategy as preCloseInterestCalculationStrategy, "
                + "lpr.id as lprId, lpr.product_id as productId, lpr.compound_type_enum as compoundType, lpr.reschedule_strategy_enum as rescheduleStrategy, "
                + "lpr.rest_frequency_type_enum as restFrequencyEnum, lpr.rest_frequency_interval as restFrequencyInterval, "
                + "lpr.rest_frequency_nth_day_enum as restFrequencyNthDayEnum, "
                + "lpr.rest_frequency_weekday_enum as restFrequencyWeekDayEnum, lpr.rest_frequency_on_day as restFrequencyOnDay, "
                + "lpr.arrears_based_on_original_schedule as isArrearsBasedOnOriginalSchedule, "
                + "lpr.compounding_frequency_type_enum as compoundingFrequencyTypeEnum, lpr.compounding_frequency_interval as compoundingInterval, "
                + "lpr.compounding_frequency_nth_day_enum as compoundingFrequencyNthDayEnum, "
                + "lpr.compounding_frequency_weekday_enum as compoundingFrequencyWeekDayEnum, "
                + "lpr.compounding_frequency_on_day as compoundingFrequencyOnDay, "
                + "lpr.is_compounding_to_be_posted_as_transaction as isCompoundingToBePostedAsTransaction, "
                + "lpr.allow_compounding_on_eod as allowCompoundingOnEod, "
                + "lpr.disallow_interest_calc_on_past_due as disallowInterestCalculationOnPastDue, "
                + "lp.hold_guarantee_funds as holdGuaranteeFunds, lp.interest_recognition_on_disbursement_date as interestRecognitionOnDisbursementDate, "
                + "lp.principal_threshold_for_last_installment as principalThresholdForLastInstallment, "
                + "lp.fixed_principal_percentage_per_installment fixedPrincipalPercentagePerInstallment, "
                + "lp.sync_expected_with_disbursement_date as syncExpectedWithDisbursementDate, "
                + "lpg.id as lpgId, lpg.mandatory_guarantee as mandatoryGuarantee, "
                + "lp.days_in_year_custom_strategy as daysInYearCustomStrategy, "
                + "lpg.minimum_guarantee_from_own_funds as minimumGuaranteeFromOwnFunds, lpg.minimum_guarantee_from_guarantor_funds as minimumGuaranteeFromGuarantor, "
                + "lp.account_moves_out_of_npa_only_on_arrears_completion as accountMovesOutOfNPAOnlyOnArrearsCompletion, "
                + "curr.name as currencyName, curr.internationalized_name_code as currencyNameCode, curr.display_symbol as currencyDisplaySymbol, lp.external_id as externalId, "
                + "lca.id as lcaId, lca.amortization_method_enum as amortizationBoolean, lca.interest_method_enum as interestMethodConfigBoolean, "
                + "lca.loan_transaction_strategy_code as transactionProcessingStrategyBoolean,lca.interest_calculated_in_period_enum as interestCalcPeriodBoolean, lca.arrearstolerance_amount as arrearsToleranceBoolean, "
                + "lca.repay_every as repaymentFrequencyBoolean, lca.moratorium as graceOnPrincipalAndInterestBoolean, lca.grace_on_arrears_ageing as graceOnArrearsAgingBoolean, "
                + "lp.is_linked_to_floating_interest_rates as isLinkedToFloatingInterestRates, "
                + "lfr.floating_rates_id as floatingRateId, fr.name as floatingRateName, "
                + "lfr.interest_rate_differential as interestRateDifferential, "
                + "lfr.min_differential_lending_rate as minDifferentialLendingRate, "
                + "lfr.default_differential_lending_rate as defaultDifferentialLendingRate, "
                + "lfr.max_differential_lending_rate as maxDifferentialLendingRate, "
                + "lfr.is_floating_interest_rate_calculation_allowed as isFloatingInterestRateCalculationAllowed, "
                + "lp.allow_variabe_installments as isVariableIntallmentsAllowed, lvi.minimum_gap as minimumGap, "
                + "lvi.maximum_gap as maximumGap, dbuc.id as delinquencyBucketId, dbuc.name as delinquencyBucketName, "
                + "lp.can_use_for_topup as canUseForTopup, lp.is_equal_amortization as isEqualAmortization, lp.loan_schedule_type as loanScheduleType, lp.loan_schedule_processing_type as loanScheduleProcessingType, lp.supported_interest_refund_types as supportedInterestRefundTypes, "
                + "lp.charge_off_behaviour as chargeOffBehaviour, " + "lp.enable_income_capitalization as enableIncomeCapitalization, "
                + "lp.capitalized_income_calculation_type as capitalizedIncomeCalculationType, "
                + "lp.capitalized_income_strategy as capitalizedIncomeStrategy, "
                + "lp.capitalized_income_type as capitalizedIncomeType, lp.is_merchant_buy_down_fee as merchantBuyDownFee, "
                + "lp.enable_buy_down_fee as enableBuyDownFee, lp.buy_down_fee_calculation_type as buyDownFeeCalculationType, "
                + "lp.buy_down_fee_strategy as buyDownFeeStrategy, lp.buy_down_fee_income_type as buyDownFeeIncomeType "
                + " from m_product_loan lp left join m_fund f on f.id = lp.fund_id "
                + " left join m_product_loan_recalculation_details lpr on lpr.product_id=lp.id "
                + " left join m_product_loan_guarantee_details lpg on lpg.loan_product_id=lp.id "
                + " left join m_product_loan_configurable_attributes lca on lca.loan_product_id = lp.id "
                + " left join m_product_loan_floating_rates as lfr on lfr.loan_product_id = lp.id "
                + " left join m_floating_rates as fr on lfr.floating_rates_id = fr.id "
                + " left join m_product_loan_variable_installment_config as lvi on lvi.loan_product_id = lp.id "
                + " left join m_delinquency_bucket as dbuc on dbuc.id = lp.delinquency_bucket_id "
                + " join m_currency curr on curr.code = lp.currency_code";
    }

    private RowMapper<LoanProductData> loanProductMapper(final List<ChargeData> charges,
            final List<LoanProductBorrowerCycleVariationData> borrowerCycleVariationDatas, final List<RateData> rates,
            final List<DelinquencyBucketData> delinquencyBucketOptions, List<AdvancedPaymentData> advancedPaymentData,
            List<CreditAllocationData> creditAllocationData) {
        return (rs, rowNum) -> {
            final Long id = JdbcSupport.getLong(rs, "id");
            final String name = rs.getString("name");
            final String shortName = rs.getString("shortName");
            final String description = rs.getString("description");
            final Long fundId = JdbcSupport.getLong(rs, "fundId");
            final String fundName = rs.getString("fundName");
            final String transactionStrategyCode = rs.getString("transactionStrategyCode");
            final String transactionStrategyName = rs.getString("transactionStrategyName");

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");

            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf, currencyDisplaySymbol,
                    currencyNameCode);

            final BigDecimal principal = rs.getBigDecimal("principal");
            final BigDecimal minPrincipal = rs.getBigDecimal("minPrincipal");
            final BigDecimal maxPrincipal = rs.getBigDecimal("maxPrincipal");
            final BigDecimal tolerance = rs.getBigDecimal("tolerance");

            final Integer numberOfRepayments = JdbcSupport.getInteger(rs, "numberOfRepayments");
            final Integer minNumberOfRepayments = JdbcSupport.getInteger(rs, "minNumberOfRepayments");
            final Integer maxNumberOfRepayments = JdbcSupport.getInteger(rs, "maxNumberOfRepayments");
            final Integer repaymentEvery = JdbcSupport.getInteger(rs, "repaidEvery");
            final Integer fixedLength = JdbcSupport.getInteger(rs, "fixedLength");

            final Integer graceOnPrincipalPayment = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "graceOnPrincipalPayment");
            final Integer recurringMoratoriumOnPrincipalPeriods = JdbcSupport.getIntegerDefaultToNullIfZero(rs,
                    "recurringMoratoriumOnPrincipalPeriods");
            final Integer graceOnInterestPayment = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "graceOnInterestPayment");
            final Integer graceOnInterestCharged = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "graceOnInterestCharged");
            final Integer graceOnArrearsAgeing = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "graceOnArrearsAgeing");
            final Integer overdueDaysForNPA = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "overdueDaysForNPA");
            final Integer minimumDaysBetweenDisbursalAndFirstRepayment = JdbcSupport.getInteger(rs,
                    "minimumDaysBetweenDisbursalAndFirstRepayment");

            final Integer accountingRuleId = JdbcSupport.getInteger(rs, "accountingType");
            final EnumOptionData accountingRuleType = AccountingEnumerations.accountingRuleType(accountingRuleId);

            final BigDecimal interestRatePerPeriod = rs.getBigDecimal("interestRatePerPeriod");
            final BigDecimal minInterestRatePerPeriod = rs.getBigDecimal("minInterestRatePerPeriod");
            final BigDecimal maxInterestRatePerPeriod = rs.getBigDecimal("maxInterestRatePerPeriod");
            final BigDecimal annualInterestRate = rs.getBigDecimal("annualInterestRate");

            final boolean isLinkedToFloatingInterestRates = rs.getBoolean("isLinkedToFloatingInterestRates");
            final Integer floatingRateId = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "floatingRateId");
            final String floatingRateName = rs.getString("floatingRateName");
            final BigDecimal interestRateDifferential = rs.getBigDecimal("interestRateDifferential");
            final BigDecimal minDifferentialLendingRate = rs.getBigDecimal("minDifferentialLendingRate");
            final BigDecimal defaultDifferentialLendingRate = rs.getBigDecimal("defaultDifferentialLendingRate");
            final BigDecimal maxDifferentialLendingRate = rs.getBigDecimal("maxDifferentialLendingRate");
            final boolean isFloatingInterestRateCalculationAllowed = rs.getBoolean("isFloatingInterestRateCalculationAllowed");

            final boolean isVariableIntallmentsAllowed = rs.getBoolean("isVariableIntallmentsAllowed");
            final Integer minimumGap = rs.getInt("minimumGap");
            final Integer maximumGap = rs.getInt("maximumGap");

            final int repaymentFrequencyTypeId = JdbcSupport.getInteger(rs, "repaymentPeriodFrequency");
            final EnumOptionData repaymentFrequencyType = LoanEnumerations.repaymentFrequencyType(repaymentFrequencyTypeId);

            final int amortizationTypeId = JdbcSupport.getInteger(rs, "amortizationMethod");
            final EnumOptionData amortizationType = LoanEnumerations.amortizationType(amortizationTypeId);
            final boolean isEqualAmortization = rs.getBoolean("isEqualAmortization");

            final Integer interestRateFrequencyTypeId = JdbcSupport.getInteger(rs, "interestRatePerPeriodFreq");
            final EnumOptionData interestRateFrequencyType = LoanEnumerations.interestRateFrequencyType(interestRateFrequencyTypeId);

            final int interestTypeId = JdbcSupport.getInteger(rs, "interestMethod");
            final EnumOptionData interestType = LoanEnumerations.interestType(interestTypeId);

            final int interestCalculationPeriodTypeId = JdbcSupport.getInteger(rs, "interestCalculationInPeriodMethod");
            final Boolean allowPartialPeriodInterestCalcualtion = rs.getBoolean("allowPartialPeriodInterestCalcualtion");
            final EnumOptionData interestCalculationPeriodType = LoanEnumerations
                    .interestCalculationPeriodType(interestCalculationPeriodTypeId);

            final boolean includeInBorrowerCycle = rs.getBoolean("includeInBorrowerCycle");
            final boolean useBorrowerCycle = rs.getBoolean("useBorrowerCycle");
            final LocalDate startDate = JdbcSupport.getLocalDate(rs, "startDate");
            final LocalDate closeDate = JdbcSupport.getLocalDate(rs, "closeDate");
            final Integer dueDaysForRepaymentEvent = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "dueDaysForRepaymentEvent");
            final Integer overDueDaysForRepaymentEvent = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "overDueDaysForRepaymentEvent");
            final boolean enableDownPayment = rs.getBoolean("enableDownPayment");
            final BigDecimal disbursedAmountPercentageForDownPayment = rs.getBigDecimal("disbursedAmountPercentageForDownPayment");
            final boolean enableAutoRepaymentForDownPayment = rs.getBoolean("enableAutoRepaymentForDownPayment");
            final Integer repaymentStartDateTypeId = JdbcSupport.getInteger(rs, "repaymentStartDateType");
            final EnumOptionData repaymentStartDateType = LoanEnumerations.repaymentStartDateType(repaymentStartDateTypeId);
            final boolean enableInstallmentLevelDelinquency = rs.getBoolean("enableInstallmentLevelDelinquency");

            String status = "";
            if (closeDate != null && DateUtils.isBeforeBusinessDate(closeDate)) {
                status = "loanProduct.inActive";
            } else {
                status = "loanProduct.active";
            }
            final String externalId = rs.getString("externalId");
            final List<LoanProductBorrowerCycleVariationData> principalVariationsForBorrowerCycle = new ArrayList<>();
            final List<LoanProductBorrowerCycleVariationData> interestRateVariationsForBorrowerCycle = new ArrayList<>();
            final List<LoanProductBorrowerCycleVariationData> numberOfRepaymentVariationsForBorrowerCycle = new ArrayList<>();
            if (this.borrowerCycleVariationDatas != null) {
                for (final LoanProductBorrowerCycleVariationData borrowerCycleVariationData : this.borrowerCycleVariationDatas) {
                    final LoanProductParamType loanProductParamType = borrowerCycleVariationData.getLoanProductParamType();
                    if (loanProductParamType.isParamTypePrincipal()) {
                        principalVariationsForBorrowerCycle.add(borrowerCycleVariationData);
                    } else if (loanProductParamType.isParamTypeInterestTate()) {
                        interestRateVariationsForBorrowerCycle.add(borrowerCycleVariationData);
                    } else if (loanProductParamType.isParamTypeRepayment()) {
                        numberOfRepaymentVariationsForBorrowerCycle.add(borrowerCycleVariationData);
                    }
                }
            }

            final Boolean multiDisburseLoan = rs.getBoolean("multiDisburseLoan");
            final Integer maxTrancheCount = rs.getInt("maxTrancheCount");
            final BigDecimal outstandingLoanBalance = rs.getBigDecimal("outstandingLoanBalance");
            final Boolean disallowExpectedDisbursements = rs.getBoolean("disallowExpectedDisbursements");
            final Boolean allowApprovedDisbursedAmountsOverApplied = rs.getBoolean("allowApprovedDisbursedAmountsOverApplied");
            final String overAppliedCalculationType = rs.getString("overAppliedCalculationType");
            final Integer overAppliedNumber = rs.getInt("overAppliedNumber");

            final int daysInMonth = JdbcSupport.getInteger(rs, "daysInMonth");
            final EnumOptionData daysInMonthType = CommonEnumerations.daysInMonthType(daysInMonth);
            final int daysInYear = JdbcSupport.getInteger(rs, "daysInYear");
            final StringEnumOptionData daysInYearCustomStrategy = ApiFacingEnum.getStringEnumOptionData(DaysInYearCustomStrategyType.class,
                    rs.getString("daysInYearCustomStrategy"));
            final EnumOptionData daysInYearType = CommonEnumerations.daysInYearType(daysInYear);
            final Integer installmentAmountInMultiplesOf = JdbcSupport.getInteger(rs, "installmentAmountInMultiplesOf");
            final boolean canDefineInstallmentAmount = rs.getBoolean("canDefineInstallmentAmount");
            final boolean isInterestRecalculationEnabled = rs.getBoolean("isInterestRecalculationEnabled");

            LoanProductInterestRecalculationData interestRecalculationData = null;
            if (isInterestRecalculationEnabled) {

                final Long lprId = JdbcSupport.getLong(rs, "lprId");
                final Long productId = JdbcSupport.getLong(rs, "productId");
                final int compoundTypeEnumValue = JdbcSupport.getInteger(rs, "compoundType");
                final EnumOptionData interestRecalculationCompoundingType = LoanEnumerations
                        .interestRecalculationCompoundingType(compoundTypeEnumValue);
                final int rescheduleStrategyEnumValue = JdbcSupport.getInteger(rs, "rescheduleStrategy");
                final EnumOptionData rescheduleStrategyType = LoanEnumerations.rescheduleStrategyType(rescheduleStrategyEnumValue);
                final int restFrequencyEnumValue = JdbcSupport.getInteger(rs, "restFrequencyEnum");
                final EnumOptionData restFrequencyType = LoanEnumerations.interestRecalculationFrequencyType(restFrequencyEnumValue);
                final int restFrequencyInterval = JdbcSupport.getInteger(rs, "restFrequencyInterval");
                final Integer restFrequencyNthDayEnumValue = JdbcSupport.getInteger(rs, "restFrequencyNthDayEnum");
                EnumOptionData restFrequencyNthDayEnum = null;
                if (restFrequencyNthDayEnumValue != null) {
                    restFrequencyNthDayEnum = LoanEnumerations.interestRecalculationCompoundingNthDayType(restFrequencyNthDayEnumValue);
                }
                final Integer restFrequencyWeekDayEnumValue = JdbcSupport.getInteger(rs, "restFrequencyWeekDayEnum");
                EnumOptionData restFrequencyWeekDayEnum = null;
                if (restFrequencyWeekDayEnumValue != null) {
                    restFrequencyWeekDayEnum = LoanEnumerations
                            .interestRecalculationCompoundingDayOfWeekType(restFrequencyWeekDayEnumValue);
                }
                final Integer restFrequencyOnDay = JdbcSupport.getInteger(rs, "restFrequencyOnDay");
                final Integer compoundingFrequencyEnumValue = JdbcSupport.getInteger(rs, "compoundingFrequencyTypeEnum");
                EnumOptionData compoundingFrequencyType = null;
                if (compoundingFrequencyEnumValue != null) {
                    compoundingFrequencyType = LoanEnumerations.interestRecalculationFrequencyType(compoundingFrequencyEnumValue);
                }
                final Integer compoundingInterval = JdbcSupport.getInteger(rs, "compoundingInterval");
                final Integer compoundingFrequencyNthDayEnumValue = JdbcSupport.getInteger(rs, "compoundingFrequencyNthDayEnum");
                EnumOptionData compoundingFrequencyNthDayEnum = null;
                if (compoundingFrequencyNthDayEnumValue != null) {
                    compoundingFrequencyNthDayEnum = LoanEnumerations
                            .interestRecalculationCompoundingNthDayType(compoundingFrequencyNthDayEnumValue);
                }
                final Integer compoundingFrequencyWeekDayEnumValue = JdbcSupport.getInteger(rs, "compoundingFrequencyWeekDayEnum");
                EnumOptionData compoundingFrequencyWeekDayEnum = null;
                if (compoundingFrequencyWeekDayEnumValue != null) {
                    compoundingFrequencyWeekDayEnum = LoanEnumerations
                            .interestRecalculationCompoundingDayOfWeekType(compoundingFrequencyWeekDayEnumValue);
                }
                final Integer compoundingFrequencyOnDay = JdbcSupport.getInteger(rs, "compoundingFrequencyOnDay");
                final boolean isArrearsBasedOnOriginalSchedule = rs.getBoolean("isArrearsBasedOnOriginalSchedule");
                final boolean isCompoundingToBePostedAsTransaction = rs.getBoolean("isCompoundingToBePostedAsTransaction");
                final int preCloseInterestCalculationStrategyEnumValue = JdbcSupport.getInteger(rs, "preCloseInterestCalculationStrategy");
                final EnumOptionData preCloseInterestCalculationStrategy = LoanEnumerations
                        .preCloseInterestCalculationStrategy(preCloseInterestCalculationStrategyEnumValue);
                final boolean allowCompoundingOnEod = rs.getBoolean("allowCompoundingOnEod");
                final boolean disallowInterestCalculationOnPastDue = rs.getBoolean("disallowInterestCalculationOnPastDue");

                interestRecalculationData = new LoanProductInterestRecalculationData(lprId, productId, interestRecalculationCompoundingType,
                        rescheduleStrategyType, restFrequencyType, restFrequencyInterval, restFrequencyNthDayEnum, restFrequencyWeekDayEnum,
                        restFrequencyOnDay, compoundingFrequencyType, compoundingInterval, compoundingFrequencyNthDayEnum,
                        compoundingFrequencyWeekDayEnum, compoundingFrequencyOnDay, isArrearsBasedOnOriginalSchedule,
                        isCompoundingToBePostedAsTransaction, preCloseInterestCalculationStrategy, allowCompoundingOnEod,
                        disallowInterestCalculationOnPastDue);
            }

            final boolean amortization = rs.getBoolean("amortizationBoolean");
            final boolean interestMethod = rs.getBoolean("interestMethodConfigBoolean");
            final boolean transactionProcessingStrategy = rs.getBoolean("transactionProcessingStrategyBoolean");
            final boolean interestCalcPeriod = rs.getBoolean("interestCalcPeriodBoolean");
            final boolean arrearsTolerance = rs.getBoolean("arrearsToleranceBoolean");
            final boolean repaymentFrequency = rs.getBoolean("repaymentFrequencyBoolean");
            final boolean graceOnPrincipalAndInterest = rs.getBoolean("graceOnPrincipalAndInterestBoolean");
            final boolean graceOnArrearsAging = rs.getBoolean("graceOnArrearsAgingBoolean");

            LoanProductConfigurableAttributes allowAttributeOverrides = null;

            allowAttributeOverrides = new LoanProductConfigurableAttributes(amortization, interestMethod, transactionProcessingStrategy,
                    interestCalcPeriod, arrearsTolerance, repaymentFrequency, graceOnPrincipalAndInterest, graceOnArrearsAging);

            final boolean holdGuaranteeFunds = rs.getBoolean("holdGuaranteeFunds");
            LoanProductGuaranteeData loanProductGuaranteeData = null;
            if (holdGuaranteeFunds) {
                final Long lpgId = JdbcSupport.getLong(rs, "lpgId");
                final BigDecimal mandatoryGuarantee = rs.getBigDecimal("mandatoryGuarantee");
                final BigDecimal minimumGuaranteeFromOwnFunds = rs.getBigDecimal("minimumGuaranteeFromOwnFunds");
                final BigDecimal minimumGuaranteeFromGuarantor = rs.getBigDecimal("minimumGuaranteeFromGuarantor");
                loanProductGuaranteeData = LoanProductGuaranteeData.instance(lpgId, id, mandatoryGuarantee, minimumGuaranteeFromOwnFunds,
                        minimumGuaranteeFromGuarantor);
            }

            final BigDecimal principalThresholdForLastInstallment = rs.getBigDecimal("principalThresholdForLastInstallment");
            final BigDecimal fixedPrincipalPercentagePerInstallment = rs.getBigDecimal("fixedPrincipalPercentagePerInstallment");
            final boolean accountMovesOutOfNPAOnlyOnArrearsCompletion = rs.getBoolean("accountMovesOutOfNPAOnlyOnArrearsCompletion");
            final boolean syncExpectedWithDisbursementDate = rs.getBoolean("syncExpectedWithDisbursementDate");

            final boolean canUseForTopup = rs.getBoolean("canUseForTopup");
            final List<RateData> rateOptions = null;
            final boolean isRatesEnabled = false;

            // Delinquency Buckets
            final Long delinquencyBucketId = JdbcSupport.getLong(rs, "delinquencyBucketId");
            final String delinquencyBucketName = rs.getString("delinquencyBucketName");
            final DelinquencyBucketData delinquencyBucket = new DelinquencyBucketData(delinquencyBucketId, delinquencyBucketName,
                    new ArrayList<>());

            final String loanScheduleTypeStr = rs.getString("loanScheduleType");
            final LoanScheduleType loanScheduleType = LoanScheduleType.valueOf(loanScheduleTypeStr);
            final String loanScheduleProcessingTypeStr = rs.getString("loanScheduleProcessingType");
            final LoanScheduleProcessingType loanScheduleProcessingType = LoanScheduleProcessingType.valueOf(loanScheduleProcessingTypeStr);
            final boolean enableAccrualActivityPosting = rs.getBoolean("enableAccrualActivityPosting");
            final String supportedInterestRefundTypesString = rs.getString("supportedInterestRefundTypes");
            List<StringEnumOptionData> supportedInterestRefundTypes = List.of();
            if (supportedInterestRefundTypesString != null && !supportedInterestRefundTypesString.isEmpty()) {
                supportedInterestRefundTypes = Arrays.stream(supportedInterestRefundTypesString.split(","))
                        .map(LoanSupportedInterestRefundTypes::valueOf)
                        .map(LoanSupportedInterestRefundTypes::getValueAsStringEnumOptionData).toList();
            }
            final String chargeOffBehaviourStr = rs.getString("chargeOffBehaviour");
            final LoanChargeOffBehaviour loanChargeOffBehaviour = LoanChargeOffBehaviour.valueOf(chargeOffBehaviourStr);
            final boolean interestRecognitionOnDisbursementDate = rs.getBoolean("interestRecognitionOnDisbursementDate");
            final boolean enableIncomeCapitalization = rs.getBoolean("enableIncomeCapitalization");
            final StringEnumOptionData capitalizedIncomeCalculationType = ApiFacingEnum
                    .getStringEnumOptionData(LoanCapitalizedIncomeCalculationType.class, rs.getString("capitalizedIncomeCalculationType"));
            final StringEnumOptionData capitalizedIncomeStrategy = ApiFacingEnum
                    .getStringEnumOptionData(LoanCapitalizedIncomeStrategy.class, rs.getString("capitalizedIncomeStrategy"));
            final StringEnumOptionData capitalizedIncome = ApiFacingEnum.getStringEnumOptionData(LoanCapitalizedIncomeType.class,
                    rs.getString("capitalizedIncomeType"));
            final boolean enableBuyDownFee = rs.getBoolean("enableBuyDownFee");
            final StringEnumOptionData buyDownFeeCalculationType = ApiFacingEnum
                    .getStringEnumOptionData(LoanBuyDownFeeCalculationType.class, rs.getString("buyDownFeeCalculationType"));
            final StringEnumOptionData buyDownFeeStrategy = ApiFacingEnum.getStringEnumOptionData(LoanBuyDownFeeStrategy.class,
                    rs.getString("buyDownFeeStrategy"));
            final StringEnumOptionData buyDownFeeIncomeType = ApiFacingEnum.getStringEnumOptionData(LoanBuyDownFeeIncomeType.class,
                    rs.getString("buyDownFeeIncomeType"));
            final boolean merchantBuyDownFee = rs.getBoolean("merchantBuyDownFee");

            return new LoanProductData(id, name, shortName, description, currency, principal, minPrincipal, maxPrincipal, tolerance,
                    numberOfRepayments, minNumberOfRepayments, maxNumberOfRepayments, repaymentEvery, interestRatePerPeriod,
                    minInterestRatePerPeriod, maxInterestRatePerPeriod, annualInterestRate, repaymentFrequencyType,
                    interestRateFrequencyType, amortizationType, interestType, interestCalculationPeriodType,
                    allowPartialPeriodInterestCalcualtion, fundId, fundName, transactionStrategyCode, transactionStrategyName,
                    graceOnPrincipalPayment, recurringMoratoriumOnPrincipalPeriods, graceOnInterestPayment, graceOnInterestCharged, null,
                    accountingRuleType, includeInBorrowerCycle, useBorrowerCycle, startDate, closeDate, status, externalId,
                    principalVariationsForBorrowerCycle, interestRateVariationsForBorrowerCycle,
                    numberOfRepaymentVariationsForBorrowerCycle, multiDisburseLoan, maxTrancheCount, outstandingLoanBalance,
                    disallowExpectedDisbursements, allowApprovedDisbursedAmountsOverApplied, overAppliedCalculationType, overAppliedNumber,
                    graceOnArrearsAgeing, overdueDaysForNPA, daysInMonthType, daysInYearType, isInterestRecalculationEnabled,
                    interestRecalculationData, minimumDaysBetweenDisbursalAndFirstRepayment, holdGuaranteeFunds, loanProductGuaranteeData,
                    principalThresholdForLastInstallment, accountMovesOutOfNPAOnlyOnArrearsCompletion, canDefineInstallmentAmount,
                    installmentAmountInMultiplesOf, allowAttributeOverrides, isLinkedToFloatingInterestRates, floatingRateId,
                    floatingRateName, interestRateDifferential, minDifferentialLendingRate, defaultDifferentialLendingRate,
                    maxDifferentialLendingRate, isFloatingInterestRateCalculationAllowed, isVariableIntallmentsAllowed, minimumGap,
                    maximumGap, syncExpectedWithDisbursementDate, canUseForTopup, isEqualAmortization, rateOptions, null, isRatesEnabled,
                    fixedPrincipalPercentagePerInstallment, null, delinquencyBucket, dueDaysForRepaymentEvent, overDueDaysForRepaymentEvent,
                    enableDownPayment, disbursedAmountPercentageForDownPayment, enableAutoRepaymentForDownPayment, null, null,
                    repaymentStartDateType, enableInstallmentLevelDelinquency, loanScheduleType.asEnumOptionData(),
                    loanScheduleProcessingType.asEnumOptionData(), fixedLength, enableAccrualActivityPosting, supportedInterestRefundTypes,
                    loanChargeOffBehaviour.getValueAsStringEnumOptionData(), interestRecognitionOnDisbursementDate,
                    daysInYearCustomStrategy, enableIncomeCapitalization, capitalizedIncomeCalculationType, capitalizedIncomeStrategy,
                    capitalizedIncome, enableBuyDownFee, buyDownFeeCalculationType, buyDownFeeStrategy, buyDownFeeIncomeType,
                    merchantBuyDownFee, null, null);
        };
    }
}
