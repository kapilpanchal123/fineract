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

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PaymentAllocationTransactionTypeENUM implements Serializable {

    DEFAULT(null, "Default"), //
    REPAYMENT(LoanTransactionTypeENUM.REPAYMENT, "Repayment"), //
    DOWN_PAYMENT(LoanTransactionTypeENUM.DOWN_PAYMENT, "Down payment"), //
    MERCHANT_ISSUED_REFUND(LoanTransactionTypeENUM.MERCHANT_ISSUED_REFUND, "Merchant issued refund"), //
    PAYOUT_REFUND(LoanTransactionTypeENUM.PAYOUT_REFUND, "Payout refund"), //
    GOODWILL_CREDIT(LoanTransactionTypeENUM.GOODWILL_CREDIT, "Goodwill credit"), //
    CHARGE_REFUND(LoanTransactionTypeENUM.CHARGE_REFUND, "Charge refund"), //
    CHARGE_ADJUSTMENT(LoanTransactionTypeENUM.CHARGE_ADJUSTMENT, "Charge adjustment"), //
    WAIVE_INTEREST(LoanTransactionTypeENUM.WAIVE_INTEREST, "Waive interest"), //
    CHARGE_PAYMENT(LoanTransactionTypeENUM.CHARGE_PAYMENT, "Charge payment"), //
    REFUND_FOR_ACTIVE_LOAN(LoanTransactionTypeENUM.REFUND_FOR_ACTIVE_LOAN, "Refund for active loan"), //
    INTEREST_PAYMENT_WAIVER(LoanTransactionTypeENUM.INTEREST_PAYMENT_WAIVER, "Interest payment waiver"), //
    INTEREST_REFUND(LoanTransactionTypeENUM.INTEREST_REFUND, "Interest refund"), //
    CAPITALIZED_INCOME_ADJUSTMENT(LoanTransactionTypeENUM.CAPITALIZED_INCOME_ADJUSTMENT, "Capitalized income adjustment");

    @Getter
    private final LoanTransactionTypeENUM loanTransactionType;

    @Getter
    private final String humanReadableName;
}
