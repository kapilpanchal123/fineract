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
package org.apache.fineract.portfolio.account.domain;

import lombok.RequiredArgsConstructor;
import org.apache.fineract.command.core.Command;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.account.data.AccountTransferRequest;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountTransfersAssembler {

    private final AccountTransfersDetailAssembler accountTransfersDetailAssembler;

    public AccountTransferDetails assembleSavingsToSavingsTransfer(Command<AccountTransferRequest> command,
            SavingsAccount fromSavingsAccount, SavingsAccount toSavingsAccount, SavingsAccountTransaction withdrawal,
            SavingsAccountTransaction deposit) {
        AccountTransferRequest request = command.getPayload();

        final AccountTransferDetails accountTransferDetails = accountTransfersDetailAssembler.assembleSavingsToSavingsTransfer(command,
                fromSavingsAccount, toSavingsAccount);

        final Money transactionMonetaryAmount = Money.of(fromSavingsAccount.getCurrency(), request.getTransferAmount());

        final AccountTransferTransaction accountTransferTransaction = AccountTransferTransaction.savingsToSavingsTransfer(
                accountTransferDetails, withdrawal, deposit, request.getTransferDate(), transactionMonetaryAmount,
                request.getTransferDescription());
        accountTransferDetails.addAccountTransferTransaction(accountTransferTransaction);
        return accountTransferDetails;
    }

    public AccountTransferDetails assembleSavingsToLoanTransfer(final Command<AccountTransferRequest> command,
            final SavingsAccount fromSavingsAccount, final Loan toLoanAccount, final SavingsAccountTransaction withdrawal,
            final LoanTransaction loanRepaymentTransaction) {
        final AccountTransferRequest request = command.getPayload();

        final AccountTransferDetails accountTransferDetails = accountTransfersDetailAssembler.assembleSavingsToLoanTransfer(command,
                fromSavingsAccount, toLoanAccount);
        final Money transactionMonetaryAmount = Money.of(fromSavingsAccount.getCurrency(), request.getTransferAmount());

        final AccountTransferTransaction accountTransferTransaction = AccountTransferTransaction.savingsToLoanTransfer(
                accountTransferDetails, withdrawal, loanRepaymentTransaction, request.getTransferDate(), transactionMonetaryAmount,
                request.getTransferDescription());
        accountTransferDetails.addAccountTransferTransaction(accountTransferTransaction);
        return accountTransferDetails;
    }

    public AccountTransferDetails assembleLoanToSavingsTransfer(Command<AccountTransferRequest> command, Loan fromLoanAccount,
            SavingsAccount toSavingsAccount, SavingsAccountTransaction deposit, LoanTransaction loanRefundTransaction) {

        AccountTransferRequest request = command.getPayload();

        final AccountTransferDetails accountTransferDetails = accountTransfersDetailAssembler.assembleLoanToSavingsTransfer(command,
                fromLoanAccount, toSavingsAccount);

        final Money transactionMonetaryAmount = Money.of(toSavingsAccount.getCurrency(), request.getTransferAmount());

        AccountTransferTransaction accountTransferTransaction = AccountTransferTransaction.loanTosavingsTransfer(accountTransferDetails,
                deposit, loanRefundTransaction, request.getTransferDate(), transactionMonetaryAmount, request.getTransferDescription());
        accountTransferDetails.addAccountTransferTransaction(accountTransferTransaction);
        return accountTransferDetails;
    }
}
