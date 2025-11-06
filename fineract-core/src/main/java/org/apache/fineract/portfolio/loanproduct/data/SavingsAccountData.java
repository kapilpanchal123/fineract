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
public class SavingsAccountData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String accountNo;
    private EnumOptionData depositType;
    private String externalId;
    private Long groupId;
    private String groupName;
    private Long clientId;
    private String clientName;
    private Long savingsProductId;
    private String savingsProductName;
    private Long fieldOfficerId;
    private String fieldOfficerName;
    private SavingsAccountStatusEnumData status;
    private SavingsAccountSubStatusEnumData subStatus;
    private String reasonForBlock;
    private SavingsAccountApplicationTimeLineData timeline;
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
    private BigDecimal onHoldFunds;
    private boolean withHoldTax;
    private TaxGroupData taxGroup;
    private LocalDate lastActiveTransactionDate;
    private boolean isDormancyTrackingActive;
    private Integer daysToInactive;
    private Integer daysToDormancy;
    private Integer daysToEscheat;
    private BigDecimal savingsAmountOnHold;

    // associations
    private SavingsAccountSummaryData summary;
    private List<SavingsAccountTransactionData> transactions;
    private List<SavingsAccountChargeData> charges;

    // template
    private List<SavingsProductData> productOptions;
    private List<StaffData> fieldOfficerOptions;
    private List<EnumOptionData> interestCompoundingPeriodTypeOptions;
    private List<EnumOptionData> interestPostingPeriodTypeOptions;
    private List<EnumOptionData> interestCalculationTypeOptions;
    private List<EnumOptionData> interestCalculationDaysInYearTypeOptions;
    private List<EnumOptionData> lockinPeriodFrequencyTypeOptions;
    private List<EnumOptionData> withdrawalFeeTypeOptions;
    private List<ChargeData> chargeOptions;

    private SavingsAccountChargeData withdrawalFee;
    private SavingsAccountChargeData annualFee;
    private BigDecimal nominalAnnualInterestRateOverdraft;
    private BigDecimal minOverdraftForInterestCalculation;
    private transient List<SavingsAccountTransactionData> savingsAccountTransactionData;

    private transient SavingsAccountTransactionData lastSavingsAccountTransaction;

    private List<DatatableData> datatables;

    // import field
    private Long productId;
    private String locale;
    private String dateFormat;
    private transient Integer rowIndex;
    private transient LocalDate startInterestCalculationDate;
    private LocalDate submittedOnDate;
    /** Uses a @Component inside of a DTO **/
    // private transient SavingsAccountTransactionDataSummaryWrapper savingsAccountTransactionSummaryWrapper;
    // private transient SavingsHelper savingsHelper;

    private transient SavingsAccountSummaryData savingsAccountSummaryData;
    private transient LocalDate activatedOnDate;
    private transient LocalDate lockedInUntilDate;
    private transient ClientData clientData;
    private transient SavingsProductData savingsProductData;
    private transient List<SavingsAccountTransactionData> newSavingsAccountTransactionData;
    private transient GroupGeneralData groupGeneralData;
    private transient Long officeId;
    private transient Set<Long> existingTransactionIds;
    private transient Set<Long> existingReversedTransactionIds;
    private transient Long glAccountIdForSavingsControl;
    private transient Long glAccountIdForInterestOnSavings;

    private Long glAccountIdForInterestPayable;
    private Long glAccountIdForOverdraftPortfolio;
    private Long glAccountIdForInterestReceivable;

    private BigDecimal interestPosting;
    private BigDecimal overdraftPosting;
}
