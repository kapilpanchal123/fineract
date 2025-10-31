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
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepositoryWrapper;
import org.apache.fineract.portfolio.account.data.AccountTransferRequest;
import org.apache.fineract.portfolio.account.data.RefundByTransferRequest;
import org.apache.fineract.portfolio.account.data.StandingInstructionCreateRequest;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.service.LoanAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountTransfersDetailAssembler {

    private final ClientRepositoryWrapper clientRepository;
    private final OfficeRepositoryWrapper officeRepositoryWrapper;
    private final SavingsAccountAssembler savingsAccountAssembler;
    private final LoanAssembler loanAccountAssembler;

    public AccountTransferDetails assembleSavingsToSavingsTransfer(final Command<AccountTransferRequest> command) {

        final AccountTransferRequest request = command.getPayload();
        final Long fromSavingsId = request.getFromAccountId();
        final SavingsAccount fromSavingsAccount = savingsAccountAssembler.assembleFrom(fromSavingsId, false);

        final boolean backdatedTxnsAllowedTill = false;
        final Long toSavingsId = request.getToAccountId();
        final SavingsAccount toSavingsAccount = savingsAccountAssembler.assembleFrom(toSavingsId, backdatedTxnsAllowedTill);

        return assembleSavingsToSavingsTransfer(command, fromSavingsAccount, toSavingsAccount);
    }

    public AccountTransferDetails standingInstructionsAssembleSavingsToSavingsTransfer(
            final Command<StandingInstructionCreateRequest> command) {

        final StandingInstructionCreateRequest request = command.getPayload();
        final Long fromSavingsId = request.getFromAccountId();
        final SavingsAccount fromSavingsAccount = savingsAccountAssembler.assembleFrom(fromSavingsId, false);

        final boolean backdatedTxnsAllowedTill = false;
        final Long toSavingsId = request.getToAccountId();
        final SavingsAccount toSavingsAccount = savingsAccountAssembler.assembleFrom(toSavingsId, backdatedTxnsAllowedTill);

        return standingInstructionsAssembleSavingsToSavingsTransfer(command, fromSavingsAccount, toSavingsAccount);
    }

    public AccountTransferDetails assembleSavingsToLoanTransfer(final Command<AccountTransferRequest> command) {

        final AccountTransferRequest request = command.getPayload();
        final Long fromSavingsAccountId = request.getFromAccountId();
        final boolean backdatedTxnsAllowedTill = false;
        final SavingsAccount fromSavingsAccount = savingsAccountAssembler.assembleFrom(fromSavingsAccountId, backdatedTxnsAllowedTill);

        final Long toLoanAccountId = request.getToAccountId();
        final Loan toLoanAccount = loanAccountAssembler.assembleFrom(toLoanAccountId);

        return assembleSavingsToLoanTransfer(command, fromSavingsAccount, toLoanAccount);
    }

    public AccountTransferDetails standingInstructionsAssembleSavingsToLoanTransfer(
            final Command<StandingInstructionCreateRequest> command) {

        final StandingInstructionCreateRequest request = command.getPayload();
        final Long fromSavingsAccountId = request.getFromAccountId();
        final boolean backdatedTxnsAllowedTill = false;
        final SavingsAccount fromSavingsAccount = savingsAccountAssembler.assembleFrom(fromSavingsAccountId, backdatedTxnsAllowedTill);
        final Long toLoanAccountId = request.getToAccountId();
        final Loan toLoanAccount = loanAccountAssembler.assembleFrom(toLoanAccountId);

        return standingInstructionsAssembleSavingsToLoanTransfer(command, fromSavingsAccount, toLoanAccount);
    }

    public AccountTransferDetails assembleLoanToSavingsTransfer(final Command<AccountTransferRequest> command) {

        final AccountTransferRequest request = command.getPayload();
        final Long fromLoanAccountId = request.getFromAccountId();
        final Loan fromLoanAccount = loanAccountAssembler.assembleFrom(fromLoanAccountId);
        final boolean backdatedTxnsAllowedTill = false;
        final Long toSavingsAccountId = request.getToAccountId();
        final SavingsAccount toSavingsAccount = savingsAccountAssembler.assembleFrom(toSavingsAccountId, backdatedTxnsAllowedTill);

        return assembleLoanToSavingsTransfer(command, fromLoanAccount, toSavingsAccount);
    }

    public AccountTransferDetails assembleSavingsToSavingsTransfer(final Command<AccountTransferRequest> command,
            final SavingsAccount fromSavingsAccount, final SavingsAccount toSavingsAccount) {
        final AccountTransferRequest request = command.getPayload();
        // final Office fromOffice = officeRepositoryWrapper.findOneWithNotFoundDetection(request.getFromOfficeId());
        // final Client fromClient = clientRepository.findOneWithNotFoundDetection(request.getFromClientId());
        // final Office toOffice = officeRepositoryWrapper.findOneWithNotFoundDetection(request.getToOfficeId());
        // final Client toClient = clientRepository.findOneWithNotFoundDetection(request.getToClientId());

        // final Integer transfertype = this.fromApiJsonHelper.extractIntegerNamed(transferTypeParamName, element,
        // Locale.getDefault());
        // TODO
        // return AccountTransferDetails.savingsToSavingsTransfer(fromOffice, fromClient, fromSavingsAccount, toOffice,
        // toClient,
        // toSavingsAccount, AccountTransferType.ACCOUNT_TRANSFER.getValue());
        return savingsToSavingsTransfer(fromSavingsAccount, toSavingsAccount, request.getFromOfficeId(), request.getFromClientId(),
                request.getToOfficeId(), request.getToClientId());
    }

    public AccountTransferDetails standingInstructionsAssembleSavingsToSavingsTransfer(
            final Command<StandingInstructionCreateRequest> command, final SavingsAccount fromSavingsAccount,
            final SavingsAccount toSavingsAccount) {
        final StandingInstructionCreateRequest request = command.getPayload();
        // final Office fromOffice = officeRepositoryWrapper.findOneWithNotFoundDetection(request.getFromOfficeId());
        // final Client fromClient = clientRepository.findOneWithNotFoundDetection(request.getFromClientId());
        // final Office toOffice = officeRepositoryWrapper.findOneWithNotFoundDetection(request.getToOfficeId());
        // final Client toClient = clientRepository.findOneWithNotFoundDetection(request.getToClientId());
        //
        // return AccountTransferDetails.savingsToSavingsTransfer(fromOffice, fromClient, fromSavingsAccount, toOffice,
        // toClient,
        // toSavingsAccount, AccountTransferType.ACCOUNT_TRANSFER.getValue());
        return savingsToSavingsTransfer(fromSavingsAccount, toSavingsAccount, request.getFromOfficeId(), request.getFromClientId(),
                request.getToOfficeId(), request.getToClientId());
    }

    private AccountTransferDetails savingsToSavingsTransfer(SavingsAccount fromSavingsAccount, SavingsAccount toSavingsAccount,
            Long fromOfficeId, Long fromClientId, Long toOfficeId, Long toClientId) {
        final Office fromOffice = officeRepositoryWrapper.findOneWithNotFoundDetection(fromOfficeId);
        final Client fromClient = clientRepository.findOneWithNotFoundDetection(fromClientId);
        final Office toOffice = officeRepositoryWrapper.findOneWithNotFoundDetection(toOfficeId);
        final Client toClient = clientRepository.findOneWithNotFoundDetection(toClientId);

        return AccountTransferDetails.savingsToSavingsTransfer(fromOffice, fromClient, fromSavingsAccount, toOffice, toClient,
                toSavingsAccount, AccountTransferType.ACCOUNT_TRANSFER.getValue());
    }

    public AccountTransferDetails assembleSavingsToLoanTransfer(final Command<AccountTransferRequest> command,
            final SavingsAccount fromSavingsAccount, final Loan toLoanAccount) {
        final AccountTransferRequest request = command.getPayload();
        // final Office fromOffice = officeRepositoryWrapper.findOneWithNotFoundDetection(request.getFromOfficeId());
        // final Client fromClient = clientRepository.findOneWithNotFoundDetection(request.getFromClientId());
        // final Office toOffice = officeRepositoryWrapper.findOneWithNotFoundDetection(request.getToOfficeId());
        // final Client toClient = clientRepository.findOneWithNotFoundDetection(request.getToClientId());
        //
        // return AccountTransferDetails.savingsToLoanTransfer(fromOffice, fromClient, fromSavingsAccount, toOffice,
        // toClient, toLoanAccount,
        // AccountTransferType.ACCOUNT_TRANSFER.getValue());
        return savingsToLoanTransfer(fromSavingsAccount, toLoanAccount, request.getFromOfficeId(), request.getFromClientId(),
                request.getToOfficeId(), request.getToClientId());
    }

    private AccountTransferDetails savingsToLoanTransfer(SavingsAccount fromSavingsAccount, Loan toLoanAccount, Long fromOfficeId,
            Long fromClientId, Long toOfficeId, Long toClientId) {
        final Office fromOffice = officeRepositoryWrapper.findOneWithNotFoundDetection(fromOfficeId);
        final Client fromClient = clientRepository.findOneWithNotFoundDetection(fromClientId);
        final Office toOffice = officeRepositoryWrapper.findOneWithNotFoundDetection(toOfficeId);
        final Client toClient = clientRepository.findOneWithNotFoundDetection(toClientId);

        return AccountTransferDetails.savingsToLoanTransfer(fromOffice, fromClient, fromSavingsAccount, toOffice, toClient, toLoanAccount,
                AccountTransferType.ACCOUNT_TRANSFER.getValue());
    }

    public AccountTransferDetails standingInstructionsAssembleSavingsToLoanTransfer(final Command<StandingInstructionCreateRequest> command,
            final SavingsAccount fromSavingsAccount, final Loan toLoanAccount) {
        final StandingInstructionCreateRequest request = command.getPayload();
        // final Office fromOffice = officeRepositoryWrapper.findOneWithNotFoundDetection(request.getFromOfficeId());
        // final Client fromClient = clientRepository.findOneWithNotFoundDetection(request.getFromClientId());
        // final Office toOffice = officeRepositoryWrapper.findOneWithNotFoundDetection(request.getToOfficeId());
        // final Client toClient = clientRepository.findOneWithNotFoundDetection(request.getToClientId());
        //
        // return AccountTransferDetails.savingsToLoanTransfer(fromOffice, fromClient, fromSavingsAccount, toOffice,
        // toClient, toLoanAccount,
        // AccountTransferType.ACCOUNT_TRANSFER.getValue());
        return savingsToLoanTransfer(fromSavingsAccount, toLoanAccount, request.getFromOfficeId(), request.getFromClientId(),
                request.getToOfficeId(), request.getToClientId());
    }

    public AccountTransferDetails assembleLoanToSavingsTransfer(Command<AccountTransferRequest> command, Loan fromLoanAccount,
            SavingsAccount toSavingsAccount) {
        final AccountTransferRequest request = command.getPayload();
        // final Office fromOffice = officeRepositoryWrapper.findOneWithNotFoundDetection(request.getFromOfficeId());
        // final Client fromClient = clientRepository.findOneWithNotFoundDetection(request.getToClientId());
        // final Office toOffice = officeRepositoryWrapper.findOneWithNotFoundDetection(request.getToOfficeId());
        // final Client toClient = clientRepository.findOneWithNotFoundDetection(request.getToClientId());
        //
        // return AccountTransferDetails.loanTosavingsTransfer(fromOffice, fromClient, fromLoanAccount, toOffice,
        // toClient, toSavingsAccount,
        // AccountTransferType.ACCOUNT_TRANSFER.getValue());
        return loanToSavingsTransfer(fromLoanAccount, toSavingsAccount, request.getFromOfficeId(), request.getToClientId(),
                request.getToOfficeId(), request.getToClientId());
    }

    public AccountTransferDetails assembleLoanToSavingsTransferRefund(Command<RefundByTransferRequest> command, Loan fromLoanAccount,
            SavingsAccount toSavingsAccount) {
        final RefundByTransferRequest request = command.getPayload();

        final Office fromOffice = officeRepositoryWrapper.findOneWithNotFoundDetection(request.getFromOfficeId());
        final Client fromClient = clientRepository.findOneWithNotFoundDetection(request.getToClientId());
        final Office toOffice = officeRepositoryWrapper.findOneWithNotFoundDetection(request.getToOfficeId());
        final Client toClient = clientRepository.findOneWithNotFoundDetection(request.getToClientId());

        return AccountTransferDetails.loanTosavingsTransfer(fromOffice, fromClient, fromLoanAccount, toOffice, toClient, toSavingsAccount,
                AccountTransferType.ACCOUNT_TRANSFER.getValue());
    }

    public AccountTransferDetails assembleSavingsToLoanTransfer(final SavingsAccount fromSavingsAccount, final Loan toLoanAccount,
            Integer transferType) {
        final Office fromOffice = fromSavingsAccount.office();
        final Client fromClient = fromSavingsAccount.getClient();
        final Office toOffice = toLoanAccount.getOffice();
        final Client toClient = toLoanAccount.client();

        return AccountTransferDetails.savingsToLoanTransfer(fromOffice, fromClient, fromSavingsAccount, toOffice, toClient, toLoanAccount,
                transferType);
    }

    public AccountTransferDetails assembleSavingsToSavingsTransfer(final SavingsAccount fromSavingsAccount,
            final SavingsAccount toSavingsAccount, Integer transferType) {
        final Office fromOffice = fromSavingsAccount.office();
        final Client fromClient = fromSavingsAccount.getClient();
        final Office toOffice = toSavingsAccount.office();
        final Client toClient = toSavingsAccount.getClient();

        return AccountTransferDetails.savingsToSavingsTransfer(fromOffice, fromClient, fromSavingsAccount, toOffice, toClient,
                toSavingsAccount, transferType);
    }

    public AccountTransferDetails assembleLoanToSavingsTransfer(final Loan fromLoanAccount, final SavingsAccount toSavingsAccount,
            Integer transferType) {
        final Office fromOffice = fromLoanAccount.getOffice();
        final Client fromClient = fromLoanAccount.client();
        final Office toOffice = toSavingsAccount.office();
        final Client toClient = toSavingsAccount.getClient();

        return AccountTransferDetails.loanTosavingsTransfer(fromOffice, fromClient, fromLoanAccount, toOffice, toClient, toSavingsAccount,
                transferType);
    }

    public AccountTransferDetails assembleLoanToLoanTransfer(Loan fromLoanAccount, Loan toLoanAccount, Integer transferType) {
        final Office fromOffice = fromLoanAccount.getOffice();
        final Client fromClient = fromLoanAccount.client();
        final Office toOffice = toLoanAccount.getOffice();
        final Client toClient = toLoanAccount.client();

        return AccountTransferDetails.loanToLoanTransfer(fromOffice, fromClient, fromLoanAccount, toOffice, toClient, toLoanAccount,
                transferType);
    }

    public AccountTransferDetails standingInstructionsAssembleLoanToSavingsTransfer(Command<StandingInstructionCreateRequest> command) {
        final StandingInstructionCreateRequest request = command.getPayload();

        final Long fromLoanAccountId = request.getFromAccountId();
        final Loan fromLoanAccount = loanAccountAssembler.assembleFrom(fromLoanAccountId);
        final boolean backdatedTxnsAllowedTill = false;
        final Long toSavingsAccountId = request.getToAccountId();
        final SavingsAccount toSavingsAccount = savingsAccountAssembler.assembleFrom(toSavingsAccountId, backdatedTxnsAllowedTill);

        return loanToSavingsTransfer(fromLoanAccount, toSavingsAccount, request.getFromOfficeId(), request.getToClientId(),
                request.getToOfficeId(), request.getToClientId());
    }

    private AccountTransferDetails loanToSavingsTransfer(Loan fromLoanAccount, SavingsAccount toSavingsAccount, Long fromOfficeId,
            Long fromClientId, Long toOfficeId, Long toClientId) {
        final Office fromOffice = officeRepositoryWrapper.findOneWithNotFoundDetection(fromOfficeId);
        final Client fromClient = clientRepository.findOneWithNotFoundDetection(fromClientId);
        final Office toOffice = officeRepositoryWrapper.findOneWithNotFoundDetection(toOfficeId);
        final Client toClient = clientRepository.findOneWithNotFoundDetection(toClientId);

        return AccountTransferDetails.loanTosavingsTransfer(fromOffice, fromClient, fromLoanAccount, toOffice, toClient, toSavingsAccount,
                AccountTransferType.ACCOUNT_TRANSFER.getValue());
    }
}
