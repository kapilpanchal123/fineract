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

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum LoanTransactionType {

    INVALID(0, "loanTransactionType.invalid"), //
    DISBURSEMENT(1, "loanTransactionType.disbursement"), //
    REPAYMENT(2, "loanTransactionType.repayment"), //
    CONTRA(3, "loanTransactionType.contra"), //
    WAIVE_INTEREST(4, "loanTransactionType.waiver"), //
    REPAYMENT_AT_DISBURSEMENT(5, "loanTransactionType.repaymentAtDisbursement"), //
    WRITEOFF(6, "loanTransactionType.writeOff"), //
    MARKED_FOR_RESCHEDULING(7, "loanTransactionType.marked.for.rescheduling"), //
    /**
     * This type of transactions is allowed on written-off loans where mfi still attempts to recover payments from
     * applicant after writing-off.
     */
    RECOVERY_REPAYMENT(8, "loanTransactionType.recoveryRepayment"), //
    WAIVE_CHARGES(9, "loanTransactionType.waiveCharges"), //
    /**
     * Transaction represents an Accrual (For either interest, charge or a penalty
     **/
    ACCRUAL(10, "loanTransactionType.accrual"), //
    /***
     * A Loan Transfer involves two steps, first a "initiate" Loan transfer transaction done by the Source branch
     * followed by a "complete" loan transaction initiated by the destination branch
     **/
    INITIATE_TRANSFER(12, "loanTransactionType.initiateTransfer"), //
    APPROVE_TRANSFER(13, "loanTransactionType.approveTransfer"), //
    WITHDRAW_TRANSFER(14, "loanTransactionType.withdrawTransfer"), //
    REJECT_TRANSFER(15, "loanTransactionType.rejectTransfer"), //
    REFUND(16, "loanTransactionType.refund"), //
    CHARGE_PAYMENT(17, "loanTransactionType.chargePayment"), //
    REFUND_FOR_ACTIVE_LOAN(18, "loanTransactionType.refund"), //
    INCOME_POSTING(19, "loanTransactionType.incomePosting"), //
    CREDIT_BALANCE_REFUND(20, "loanTransactionType.creditBalanceRefund"), //
    MERCHANT_ISSUED_REFUND(21, "loanTransactionType.merchantIssuedRefund"), //
    PAYOUT_REFUND(22, "loanTransactionType.payoutRefund"), //
    GOODWILL_CREDIT(23, "loanTransactionType.goodwillCredit"), //
    CHARGE_REFUND(24, "loanTransactionType.chargeRefund"), //
    CHARGEBACK(25, "loanTransactionType.chargeback"), //
    CHARGE_ADJUSTMENT(26, "loanTransactionType.chargeAdjustment"), //
    CHARGE_OFF(27, "loanTransactionType.chargeOff"), //
    DOWN_PAYMENT(28, "loanTransactionType.downPayment"), //
    REAGE(29, "loanTransactionType.reAge"), //
    REAMORTIZE(30, "loanTransactionType.reAmortize"), //
    INTEREST_PAYMENT_WAIVER(31, "loanTransactionType.interestPaymentWaiver"), //
    ACCRUAL_ACTIVITY(32, "loanTransactionType.accrualActivity"), //
    INTEREST_REFUND(33, "loanTransactionType.interestRefund"), //
    ACCRUAL_ADJUSTMENT(34, "loanTransactionType.accrualAdjustment"), //
    CAPITALIZED_INCOME(35, "loanTransactionType.capitalizedIncome"), //
    CAPITALIZED_INCOME_AMORTIZATION(36, "loanTransactionType.capitalizedIncomeAmortization"), //
    CAPITALIZED_INCOME_ADJUSTMENT(37, "loanTransactionType.capitalizedIncomeAdjustment"), //
    CONTRACT_TERMINATION(38, "loanTransactionType.contractTermination"), //
    CAPITALIZED_INCOME_AMORTIZATION_ADJUSTMENT(39, "loanTransactionType.capitalizedIncomeAmortizationAdjustment"), //
    BUY_DOWN_FEE(40, "loanTransactionType.buyDownFee"), //
    BUY_DOWN_FEE_ADJUSTMENT(41, "loanTransactionType.buyDownFeeAdjustment"), //
    BUY_DOWN_FEE_AMORTIZATION(42, "loanTransactionType.buyDownFeeAmortization"), //
    BUY_DOWN_FEE_AMORTIZATION_ADJUSTMENT(43, "loanTransactionType.buyDownFeeAmortizationAdjustment"); //

    @Getter
    private final Integer value;

    @Getter
    private final String code;
}
