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
package org.apache.fineract.portfolio.loanproduct.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.core.domain.ExternalId;
import org.apache.fineract.portfolio.loanproduct.LoanProductConstants;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductRequest;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "m_product_loan_configurable_attributes")
@Builder
public class LoanProductConfigurableAttributes extends AbstractPersistableCustom<Long> implements Serializable {

    @OneToOne
    @JoinColumn(name = "loan_product_id", nullable = false)
    private LoanProduct loanProduct;

    @Column(name = "amortization_method_enum")
    private Boolean amortizationType;

    @Column(name = "interest_method_enum")
    private Boolean interestType;

    @Column(name = "loan_transaction_strategy_code")
    private Boolean transactionProcessingStrategyCode;

    @Column(name = "interest_calculated_in_period_enum")
    private Boolean interestCalculationPeriodType;

    @Column(name = "arrearstolerance_amount")
    private Boolean inArrearsTolerance;

    @Column(name = "repay_every")
    private Boolean repaymentEvery;

    @Column(name = "moratorium")
    private Boolean graceOnPrincipalAndInterestPayment;

    @Column(name = "grace_on_arrears_ageing")
    private Boolean graceOnArrearsAgeing;

//    private static final String[] supportedLoanConfigurableAttributes = { LoanProductConstants.amortizationTypeParamName,
//            LoanProductConstants.interestTypeParamName, LoanProductConstants.transactionProcessingStrategyCodeParamName,
//            LoanProductConstants.interestCalculationPeriodTypeParamName, LoanProductConstants.inArrearsToleranceParamName,
//            LoanProductConstants.repaymentEveryParamName, LoanProductConstants.graceOnPrincipalAndInterestPaymentParamName,
//            LoanProductConstants.GRACE_ON_ARREARS_AGEING_PARAMETER_NAME };

    public static LoanProductConfigurableAttributes createFrom(JsonCommand command) {
        final Boolean amortization = command.parsedJson().getAsJsonObject()
                .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
                .getAsJsonPrimitive(LoanProductConstants.amortizationTypeParamName).getAsBoolean();
        final Boolean interestMethod = command.parsedJson().getAsJsonObject()
                .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
                .getAsJsonPrimitive(LoanProductConstants.interestTypeParamName).getAsBoolean();
        final Boolean transactionProcessingStrategy = command.parsedJson().getAsJsonObject()
                .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
                .getAsJsonPrimitive(LoanProductConstants.transactionProcessingStrategyCodeParamName).getAsBoolean();
        final Boolean interestCalcPeriod = command.parsedJson().getAsJsonObject()
                .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
                .getAsJsonPrimitive(LoanProductConstants.interestCalculationPeriodTypeParamName).getAsBoolean();
        final Boolean arrearsTolerance = command.parsedJson().getAsJsonObject()
                .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
                .getAsJsonPrimitive(LoanProductConstants.inArrearsToleranceParamName).getAsBoolean();
        final Boolean repaymentEvery = command.parsedJson().getAsJsonObject()
                .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
                .getAsJsonPrimitive(LoanProductConstants.repaymentEveryParamName).getAsBoolean();
        final Boolean graceOnPrincipalAndInterestPayment = command.parsedJson().getAsJsonObject()
                .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
                .getAsJsonPrimitive(LoanProductConstants.graceOnPrincipalAndInterestPaymentParamName).getAsBoolean();
        final Boolean graceOnArrearsAging = command.parsedJson().getAsJsonObject()
                .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
                .getAsJsonPrimitive(LoanProductConstants.GRACE_ON_ARREARS_AGEING_PARAMETER_NAME).getAsBoolean();

        return new LoanProductConfigurableAttributes(amortization, interestMethod, transactionProcessingStrategy, interestCalcPeriod,
                arrearsTolerance, repaymentEvery, graceOnPrincipalAndInterestPayment, graceOnArrearsAging);
    }

    public static LoanProductConfigurableAttributes createFrom(final LoanProductRequest loanProductRequest) {
//      final Boolean amortization = command.parsedJson().getAsJsonObject()
//          .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
//          .getAsJsonPrimitive(LoanProductConstants.amortizationTypeParamName).getAsBoolean();
      final Boolean amortization = loanProductRequest.getAllowAttributeOverrides().getAmortizationType();

//      final Boolean interestMethod = command.parsedJson().getAsJsonObject()
//          .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
//          .getAsJsonPrimitive(LoanProductConstants.interestTypeParamName).getAsBoolean();
      final Boolean interestMethod = loanProductRequest.getAllowAttributeOverrides().getInterestType();

//      final Boolean transactionProcessingStrategy = command.parsedJson().getAsJsonObject()
//          .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
//          .getAsJsonPrimitive(LoanProductConstants.transactionProcessingStrategyCodeParamName).getAsBoolean();
      final Boolean transactionProcessingStrategy = loanProductRequest.getAllowAttributeOverrides().getTransactionProcessingStrategyCode();

//      final Boolean interestCalcPeriod = command.parsedJson().getAsJsonObject()
//          .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
//          .getAsJsonPrimitive(LoanProductConstants.interestCalculationPeriodTypeParamName).getAsBoolean();
      final Boolean interestCalcPeriod = loanProductRequest.getAllowAttributeOverrides().getInterestCalculationPeriodType();

//      final Boolean arrearsTolerance = command.parsedJson().getAsJsonObject()
//          .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
//          .getAsJsonPrimitive(LoanProductConstants.inArrearsToleranceParamName).getAsBoolean();
      final Boolean arrearsTolerance = loanProductRequest.getAllowAttributeOverrides().getInArrearsTolerance();

//      final Boolean repaymentEvery = command.parsedJson().getAsJsonObject()
//          .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
//          .getAsJsonPrimitive(LoanProductConstants.repaymentEveryParamName).getAsBoolean();
      final Boolean repaymentEvery = loanProductRequest.getAllowAttributeOverrides().getRepaymentEvery();

//      final Boolean graceOnPrincipalAndInterestPayment = command.parsedJson().getAsJsonObject()
//          .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
//          .getAsJsonPrimitive(LoanProductConstants.graceOnPrincipalAndInterestPaymentParamName).getAsBoolean();
      final Boolean graceOnPrincipalAndInterestPayment = loanProductRequest.getAllowAttributeOverrides().getGraceOnPrincipalAndInterestPayment();

//      final Boolean graceOnArrearsAging = command.parsedJson().getAsJsonObject()
//          .getAsJsonObject(LoanProductConstants.allowAttributeOverridesParamName)
//          .getAsJsonPrimitive(LoanProductConstants.GRACE_ON_ARREARS_AGEING_PARAMETER_NAME).getAsBoolean();
      final Boolean graceOnArrearsAgeing = loanProductRequest.getAllowAttributeOverrides().getGraceOnArrearsAgeing();

//      return new LoanProductConfigurableAttributes(amortization, interestMethod, transactionProcessingStrategy, interestCalcPeriod,
//          arrearsTolerance, repaymentEvery, graceOnPrincipalAndInterestPayment, graceOnArrearsAgeing);

      return LoanProductConfigurableAttributes.builder()
          .amortizationType(amortization)
          .interestType(interestMethod)
          .interestCalculationPeriodType(interestCalcPeriod)
          .transactionProcessingStrategyCode(transactionProcessingStrategy)
          .inArrearsTolerance(arrearsTolerance)
          .repaymentEvery(repaymentEvery)
          .graceOnPrincipalAndInterestPayment(graceOnPrincipalAndInterestPayment)
          .graceOnArrearsAgeing(graceOnArrearsAgeing)
          .build();
    }

    public void updateLoanProduct(final LoanProduct loanProduct) {
        this.loanProduct = loanProduct;
    }

    public static LoanProductConfigurableAttributes populateDefaultsForConfigurableAttributes() {
        final Boolean amortization = Boolean.TRUE;
        final Boolean interestMethod = Boolean.TRUE;
        final Boolean transactionProcessingStrategy = Boolean.TRUE;
        final Boolean interestCalcPeriod = Boolean.TRUE;
        final Boolean arrearsTolerance = Boolean.TRUE;
        final Boolean repaymentEvery = Boolean.TRUE;
        final Boolean graceOnPrincipalAndInterestPayment = Boolean.TRUE;
        final Boolean graceOnArrearsAging = Boolean.TRUE;

        return new LoanProductConfigurableAttributes(amortization, interestMethod, transactionProcessingStrategy, interestCalcPeriod,
                arrearsTolerance, repaymentEvery, graceOnPrincipalAndInterestPayment, graceOnArrearsAging);
    }

    public LoanProductConfigurableAttributes(Boolean amortization, Boolean interestMethod, Boolean transactionProcessingStrategyCode,
            Boolean interestCalcPeriod, Boolean arrearsTolerance, Boolean repaymentEvery, Boolean graceOnPrincipalAndInterestPayment,
            Boolean graceOnArrearsAging) {
        this.amortizationType = amortization;
        this.interestType = interestMethod;
        this.inArrearsTolerance = arrearsTolerance;
        this.graceOnArrearsAgeing = graceOnArrearsAging;
        this.interestCalculationPeriodType = interestCalcPeriod;
        this.graceOnPrincipalAndInterestPayment = graceOnPrincipalAndInterestPayment;
        this.repaymentEvery = repaymentEvery;
        this.transactionProcessingStrategyCode = transactionProcessingStrategyCode;
    }

    public Boolean getAmortizationBoolean() {
        return amortizationType;
    }

    public Boolean getInterestMethodBoolean() {
        return interestType;
    }

    public Boolean getTransactionProcessingStrategyBoolean() {
        return transactionProcessingStrategyCode;
    }

    public Boolean getInterestCalcPeriodBoolean() {
        return interestCalculationPeriodType;
    }

    public Boolean getArrearsToleranceBoolean() {
        return inArrearsTolerance;
    }

    public Boolean getRepaymentEveryBoolean() {
        return repaymentEvery;
    }

    public Boolean getGraceOnPrincipalAndInterestPaymentBoolean() {
        return graceOnPrincipalAndInterestPayment;
    }

    public Boolean getGraceOnArrearsAgingBoolean() {
        return graceOnArrearsAgeing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof LoanProductConfigurableAttributes)) {
            return false;
        }

        LoanProductConfigurableAttributes that = (LoanProductConfigurableAttributes) o;

        return Objects.equals(loanProduct, that.loanProduct) && Objects.equals(amortizationType, that.amortizationType)
                && Objects.equals(interestType, that.interestType)
                && Objects.equals(transactionProcessingStrategyCode, that.transactionProcessingStrategyCode)
                && Objects.equals(interestCalculationPeriodType, that.interestCalculationPeriodType)
                && Objects.equals(inArrearsTolerance, that.inArrearsTolerance) && Objects.equals(repaymentEvery, that.repaymentEvery)
                && Objects.equals(graceOnPrincipalAndInterestPayment, that.graceOnPrincipalAndInterestPayment)
                && Objects.equals(graceOnArrearsAgeing, that.graceOnArrearsAgeing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loanProduct, amortizationType, interestType, transactionProcessingStrategyCode, interestCalculationPeriodType,
                inArrearsTolerance, repaymentEvery, graceOnPrincipalAndInterestPayment, graceOnArrearsAgeing);
    }
}
