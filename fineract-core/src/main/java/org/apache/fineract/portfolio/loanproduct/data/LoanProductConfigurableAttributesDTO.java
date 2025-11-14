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
public class LoanProductConfigurableAttributesDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private LoanProductDTO loanProduct;
    private Boolean amortizationType;
    private Boolean interestType;
    private Boolean transactionProcessingStrategyCode;
    private Boolean interestCalculationPeriodType;
    private Boolean inArrearsTolerance;
    private Boolean repaymentEvery;
    private Boolean graceOnPrincipalAndInterestPayment;
    private Boolean graceOnArrearsAgeing;

    public static LoanProductConfigurableAttributesDTO createFrom(LoanProductRequestDTO loanProductRequestDTO) {

        // final Boolean amortization = command.parsedJson().getAsJsonObject()
        // .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
        // .getAsJsonPrimitive(LoanProductConstants.amortizationTypeParamName).getAsBoolean();
        final Boolean amortization = loanProductRequestDTO.getIsEqualAmortization();

        // final Boolean interestMethod = command.parsedJson().getAsJsonObject()
        // .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
        // .getAsJsonPrimitive(LoanProductConstants.interestTypeParamName).getAsBoolean();
        final Boolean interestMethod = Boolean.getBoolean(loanProductRequestDTO.getInterestType().getCode());

        // final Boolean transactionProcessingStrategy = command.parsedJson().getAsJsonObject()
        // .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
        // .getAsJsonPrimitive(LoanProductConstants.transactionProcessingStrategyCodeParamName).getAsBoolean();
        final Boolean transactionProcessingStrategy = Boolean.getBoolean(loanProductRequestDTO.getTransactionProcessingStrategyCode());

        // final Boolean interestCalcPeriod = command.parsedJson().getAsJsonObject()
        // .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
        // .getAsJsonPrimitive(LoanProductConstants.interestCalculationPeriodTypeParamName).getAsBoolean();
        final Boolean interestCalcPeriod = Boolean.getBoolean(loanProductRequestDTO.getInterestCalculationPeriodType().getCode());

        // final Boolean arrearsTolerance = command.parsedJson().getAsJsonObject()
        // .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
        // .getAsJsonPrimitive(LoanProductConstants.inArrearsToleranceParamName).getAsBoolean();
        final Boolean arrearsTolerance = loanProductRequestDTO.getInArrearsTolerance() != null;

        // final Boolean repaymentEvery = command.parsedJson().getAsJsonObject()
        // .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
        // .getAsJsonPrimitive(LoanProductConstants.repaymentEveryParamName).getAsBoolean();
        final Boolean repaymentEvery = loanProductRequestDTO.getRepaymentEvery() != null;

        // final Boolean graceOnPrincipalAndInterestPayment = command.parsedJson().getAsJsonObject()
        // .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
        // .getAsJsonPrimitive(LoanProductConstants.graceOnPrincipalAndInterestPaymentParamName).getAsBoolean();
        final Boolean graceOnPrincipalAndInterestPayment = loanProductRequestDTO.getGraceOnPrincipalPayment() != null
                && loanProductRequestDTO.getGraceOnInterestPayment() != null;

        // final Boolean graceOnArrearsAging = command.parsedJson().getAsJsonObject()
        // .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
        // .getAsJsonPrimitive(LoanProductConstants.GRACE_ON_ARREARS_AGEING_PARAMETER_NAME).getAsBoolean();
        final Boolean graceOnArrearsAging = loanProductRequestDTO.getGraceOnArrearsAgeing() != null;

        // return new LoanProductConfigurableAttributes(amortization, interestMethod, transactionProcessingStrategy,
        // interestCalcPeriod,
        // arrearsTolerance, repaymentEvery, graceOnPrincipalAndInterestPayment, graceOnArrearsAging);

        return LoanProductConfigurableAttributesDTO.builder().amortizationType(amortization).interestType(interestMethod)
                .transactionProcessingStrategyCode(transactionProcessingStrategy).interestCalculationPeriodType(interestCalcPeriod)
                .inArrearsTolerance(arrearsTolerance).repaymentEvery(repaymentEvery)
                .graceOnPrincipalAndInterestPayment(graceOnPrincipalAndInterestPayment).graceOnArrearsAgeing(graceOnArrearsAging).build();
    }

    public static LoanProductConfigurableAttributesDTO populateDefaultsForConfigurableAttributes() {
        final Boolean amortization = true;
        final Boolean interestMethod = true;
        final Boolean transactionProcessingStrategy = true;
        final Boolean interestCalcPeriod = true;
        final Boolean arrearsTolerance = true;
        final Boolean repaymentEvery = true;
        final Boolean graceOnPrincipalAndInterestPayment = true;
        final Boolean graceOnArrearsAging = true;

        // return new LoanProductConfigurableAttributesDTO(amortization, interestMethod, transactionProcessingStrategy,
        // interestCalcPeriod,
        // arrearsTolerance, repaymentEvery, graceOnPrincipalAndInterestPayment, graceOnArrearsAging);

        return LoanProductConfigurableAttributesDTO.builder().amortizationType(amortization).interestType(interestMethod)
                .transactionProcessingStrategyCode(transactionProcessingStrategy).graceOnArrearsAgeing(arrearsTolerance)
                .repaymentEvery(repaymentEvery).graceOnPrincipalAndInterestPayment(graceOnPrincipalAndInterestPayment)
                .graceOnArrearsAgeing(graceOnArrearsAging).build();
    }
}
