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
import java.util.List;
import java.util.Map;
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
public class SavingsProductData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String shortName;
    private String description;
    private CurrencyData currency;
    private BigDecimal nominalAnnualInterestRate;
    private EnumOptionData interestCompoundingPeriodType;
    private EnumOptionData interestPostingPeriodType;
    private EnumOptionData interestCalculationType;
    private EnumOptionData interestCalculationDaysInYearType;
    private BigDecimal minRequiredOpeningBalance;
    private Integer lockinPeriodFrequency;
    private EnumOptionData lockinPeriodFrequencyType;
    private boolean withdrawalFeeForTransfers;
    private boolean allowOverdraft;
    private BigDecimal overdraftLimit;
    private BigDecimal minRequiredBalance;
    private boolean enforceMinRequiredBalance;
    private BigDecimal maxAllowedLienLimit;
    private boolean lienAllowed;
    private BigDecimal minBalanceForInterestCalculation;
    private BigDecimal nominalAnnualInterestRateOverdraft;
    private BigDecimal minOverdraftForInterestCalculation;
    private boolean withHoldTax;
    private TaxGroupData taxGroup;
    private String depositAccountType;
    private String accountMappingForPayment;

    // accounting
    private EnumOptionData accountingRule;
    private Map<String, Object> accountingMappings;
    private List<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings;
    private List<ChargeToGLAccountMapper> feeToIncomeAccountMappings;
    private List<ChargeToGLAccountMapper> penaltyToIncomeAccountMappings;

    // charges
    private List<ChargeData> charges;

    // template
    private List<CurrencyData> currencyOptions;
    private List<EnumOptionData> interestCompoundingPeriodTypeOptions;
    private List<EnumOptionData> interestPostingPeriodTypeOptions;
    private List<EnumOptionData> interestCalculationTypeOptions;
    private List<EnumOptionData> interestCalculationDaysInYearTypeOptions;
    private List<EnumOptionData> lockinPeriodFrequencyTypeOptions;
    private List<EnumOptionData> withdrawalFeeTypeOptions;
    private List<PaymentTypeData> paymentTypeOptions;
    private List<EnumOptionData> accountingRuleOptions;
    private Map<String, List<GLAccountData>> accountingMappingOptions;
    private List<ChargeData> chargeOptions;
    private List<ChargeData> penaltyOptions;
    private List<TaxGroupData> taxGroupOptions;
    private Boolean isDormancyTrackingActive;
    private Long daysToInactive;
    private Long daysToDormancy;
    private Long daysToEscheat;
}
