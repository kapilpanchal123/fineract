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
public class LoanProductDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private FundDTO fund;
    private String transactionProcessingStrategyCode;
    private String transactionProcessingStrategyName;
    private List<LoanProductPaymentAllocationRuleDTO> paymentAllocationRules;
    private List<LoanProductCreditAllocationRuleDTO> creditAllocationRules;
    private String name;
    private String shortName;
    private String description;
    private List<ChargeDTO> charges;
    private List<RateDTO> rates;
    private LoanProductRelatedDetailDTO loanProductRelatedDetail;
    private LoanProductMinMaxConstraintsDTO loanProductMinMaxConstraints;
    private AccountingRuleTypeENUM accountingRule;
    private Boolean includeInBorrowerCycle;
    private Boolean useBorrowerCycle;
    private LoanProductTrancheDetailsDTO loanProductTrancheDetails;
    private LocalDate startDate;
    private LocalDate closeDate;
    private ExternalIdDTO externalId;
    private Set<LoanProductBorrowerCycleVariationsDTO> borrowerCycleVariations;
    private Integer overdueDaysForNPA;
    private Integer minimumDaysBetweenDisbursalAndFirstRepayment;
    private LoanProductInterestRecalculationDetailsDTO productInterestRecalculationDetails;
    private Boolean holdGuaranteeFunds;
    private LoanProductGuaranteeDetailsDTO loanProductGuaranteeDetails;
    private LoanProductConfigurableAttributesDTO loanConfigurableAttributes;
    private BigDecimal principalThresholdForLastInstallment;
    private Boolean accountMovesOutOfNPAOnlyOnArrearsCompletion;
    private Boolean canDefineInstallmentAmount;
    private Boolean isLinkedToFloatingInterestRate;
    private LoanProductFloatingRatesDTO floatingRates;
    private Boolean allowVariableInstallments;
    private LoanProductVariableInstallmentConfigDTO variableInstallmentConfig;
    private Boolean syncExpectedWithDisbursementDate;
    private Boolean canUseForTopup;
    private BigDecimal fixedPrincipalPercentagePerInstallment;
    private Boolean disallowExpectedDisbursements;
    private Boolean allowApprovedDisbursedAmountsOverApplied;
    private String overAppliedCalculationType;
    private Integer overAppliedNumber;
    private DelinquencyBucketDTO delinquencyBucket;
    private Boolean enableInstallmentLevelDelinquency;
    private Integer dueDaysForRepaymentEvent;
    private Integer overDueDaysForRepaymentEvent;
    private RepaymentStartDateTypeENUM repaymentStartDateType;
}
