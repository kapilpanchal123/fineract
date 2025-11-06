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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanProductDTOData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private Long id;
    private FundData fund;
    private String transactionProcessingStrategyCode;
    private String transactionProcessingStrategyName;
    private List<LoanProductPaymentAllocationRuleData> paymentAllocationRules;
    private List<LoanProductCreditAllocationRuleData> creditAllocationRules;
    private String name;
    private String shortName;
    private String description;
    private List<ChargeData> charges;
    private List<RateData> rates;
    private LoanProductRelatedDetailData loanProductRelatedDetail;
    private LoanProductMinMaxConstraints loanProductMinMaxConstraints;
    private AccountingRuleType accountingRule;
    private boolean includeInBorrowerCycle;
    private boolean useBorrowerCycle;
    private LoanProductTrancheDetails loanProductTrancheDetails;
    private LocalDate startDate;
    private LocalDate closeDate;
    private ExternalIdData externalId;
    private Set<LoanProductBorrowerCycleVariations> borrowerCycleVariations;
    private Integer overdueDaysForNPA;
    private Integer minimumDaysBetweenDisbursalAndFirstRepayment;
    private LoanProductInterestRecalculationDetails productInterestRecalculationDetails;
    private boolean holdGuaranteeFunds;
    private LoanProductGuaranteeDetails loanProductGuaranteeDetails;
    private LoanProductConfigurableAttributes loanConfigurableAttributes;
    private BigDecimal principalThresholdForLastInstallment;
    private boolean accountMovesOutOfNPAOnlyOnArrearsCompletion;
    private boolean canDefineInstallmentAmount;
    private boolean isLinkedToFloatingInterestRate;
    private LoanProductFloatingRates floatingRates;
    private boolean allowVariableInstallments;
    private LoanProductVariableInstallmentConfig variableInstallmentConfig;
    private boolean syncExpectedWithDisbursementDate;
    private boolean canUseForTopup;
    private BigDecimal fixedPrincipalPercentagePerInstallment;
    private boolean disallowExpectedDisbursements;
    private boolean allowApprovedDisbursedAmountsOverApplied;
    private String overAppliedCalculationType;
    private Integer overAppliedNumber;
    private DelinquencyBucketData delinquencyBucket;
    private boolean enableInstallmentLevelDelinquency;
    private Integer dueDaysForRepaymentEvent;
    private Integer overDueDaysForRepaymentEvent;
    private RepaymentStartDateType repaymentStartDateType;
}
