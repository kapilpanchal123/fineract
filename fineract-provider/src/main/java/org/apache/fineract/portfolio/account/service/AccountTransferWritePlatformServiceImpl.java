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
package org.apache.fineract.portfolio.account.service;

import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.command.core.Command;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.config.FineractProperties;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.domain.ExternalId;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.service.ExternalIdFactory;
import org.apache.fineract.portfolio.account.PortfolioAccountType;
import org.apache.fineract.portfolio.account.data.AccountTransferDTO;
import org.apache.fineract.portfolio.account.data.AccountTransferRequest;
import org.apache.fineract.portfolio.account.data.AccountTransferResponse;
import org.apache.fineract.portfolio.account.data.AccountTransfersDataValidator;
import org.apache.fineract.portfolio.account.data.RefundByTransferRequest;
import org.apache.fineract.portfolio.account.data.RefundByTransferResponse;
import org.apache.fineract.portfolio.account.domain.AccountTransferAssembler;
import org.apache.fineract.portfolio.account.domain.AccountTransferDetailRepository;
import org.apache.fineract.portfolio.account.domain.AccountTransferDetails;
import org.apache.fineract.portfolio.account.domain.AccountTransferRepository;
import org.apache.fineract.portfolio.account.domain.AccountTransferTransaction;
import org.apache.fineract.portfolio.account.domain.AccountTransferType;
import org.apache.fineract.portfolio.account.domain.AccountTransfersAssembler;
import org.apache.fineract.portfolio.account.exception.DifferentCurrenciesException;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanAccountDomainService;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;
import org.apache.fineract.portfolio.loanaccount.exception.InvalidPaidInAdvanceAmountException;
import org.apache.fineract.portfolio.loanaccount.service.LoanAssembler;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.savings.SavingsTransactionBooleanValues;
import org.apache.fineract.portfolio.savings.domain.GSIMRepositoy;
import org.apache.fineract.portfolio.savings.domain.GroupSavingsIndividualMonitoring;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.apache.fineract.portfolio.savings.service.SavingsAccountDomainService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountTransferWritePlatformServiceImpl implements AccountTransferWritePlatformService {

    private static final Long DAYS_TO_ADD = 1L;

    private final AccountTransfersDataValidator accountTransfersDataValidator;
    private final AccountTransferAssembler accountTransferAssembler;
    private final AccountTransferRepository accountTransferRepository;
    private final SavingsAccountAssembler savingsAccountAssembler;
    private final SavingsAccountDomainService savingsAccountDomainService;
    private final LoanAssembler loanAccountAssembler;
    private final LoanAccountDomainService loanAccountDomainService;
    private final SavingsAccountWritePlatformService savingsAccountWritePlatformService;
    private final AccountTransferDetailRepository accountTransferDetailRepository;
    private final LoanReadPlatformService loanReadPlatformService;
    private final GSIMRepositoy gsimRepository;
    private final ConfigurationDomainService configurationDomainService;
    private final ExternalIdFactory externalIdFactory;
    private final FineractProperties fineractProperties;
    private final AccountTransfersAssembler accountTransfersAssembler;

    @Transactional
    @Override
    public AccountTransferResponse create(Command<AccountTransferRequest> command) {

        final AccountTransferRequest request = command.getPayload();
        boolean isRegularTransaction = true;

        final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(Locale.of("en"));
        final PortfolioAccountType fromAccountType = PortfolioAccountType.fromInt(request.getFromAccountType());
        final PortfolioAccountType toAccountType = PortfolioAccountType.fromInt(request.getToAccountType());

        final PaymentDetail paymentDetail = null;
        Long fromSavingsAccountId = null;
        Long transferDetailId = null;
        boolean isInterestTransfer = false;
        boolean isAccountTransfer = true;
        Long fromLoanAccountId = null;
        boolean isWithdrawBalance = false;
        final boolean backdatedTxnsAllowedTill = false;

        if (isSavingsToSavingsAccountTransfer(fromAccountType, toAccountType)) {
            fromSavingsAccountId = request.getFromAccountId();
            final SavingsAccount fromSavingsAccount = savingsAccountAssembler.assembleFrom(fromSavingsAccountId, backdatedTxnsAllowedTill);

            final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
                    isRegularTransaction, fromSavingsAccount.isWithdrawalFeeApplicableForTransfer(), isInterestTransfer, isWithdrawBalance);
            final SavingsAccountTransaction withdrawal = savingsAccountDomainService.handleWithdrawal(fromSavingsAccount, fmt,
                    request.getTransferDate(), request.getTransferAmount(), paymentDetail, transactionBooleanValues,
                    backdatedTxnsAllowedTill);

            final Long toSavingsId = request.getToAccountId();
            final SavingsAccount toSavingsAccount = savingsAccountAssembler.assembleFrom(toSavingsId, backdatedTxnsAllowedTill);

            final SavingsAccountTransaction deposit = savingsAccountDomainService.handleDeposit(toSavingsAccount, fmt,
                    request.getTransferDate(), request.getTransferAmount(), paymentDetail, isAccountTransfer, isRegularTransaction,
                    backdatedTxnsAllowedTill);

            if (!fromSavingsAccount.getCurrency().getCode().equals(toSavingsAccount.getCurrency().getCode())) {
                throw new DifferentCurrenciesException(fromSavingsAccount.getCurrency().getCode(),
                        toSavingsAccount.getCurrency().getCode());
            }

            final AccountTransferDetails accountTransferDetails = accountTransfersAssembler.assembleSavingsToSavingsTransfer(command,
                    fromSavingsAccount, toSavingsAccount, withdrawal, deposit);

            accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
            transferDetailId = accountTransferDetails.getId();

        } else if (isSavingsToLoanAccountTransfer(fromAccountType, toAccountType)) {
            fromSavingsAccountId = request.getFromAccountId();
            final SavingsAccount fromSavingsAccount = savingsAccountAssembler.assembleFrom(fromSavingsAccountId, backdatedTxnsAllowedTill);

            final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
                    isRegularTransaction, fromSavingsAccount.isWithdrawalFeeApplicableForTransfer(), isInterestTransfer, isWithdrawBalance);
            final SavingsAccountTransaction withdrawal = savingsAccountDomainService.handleWithdrawal(fromSavingsAccount, fmt,
                    request.getTransferDate(), request.getTransferAmount(), paymentDetail, transactionBooleanValues,
                    backdatedTxnsAllowedTill);

            Loan toLoanAccount = loanAccountAssembler.assembleFrom(request.getToAccountId());

            final Boolean isHolidayValidationDone = false;
            final HolidayDetailDTO holidayDetailDto = null;
            final boolean isRecoveryRepayment = false;
            final String chargeRefundChargeType = null;

            final ExternalId externalId = externalIdFactory.create();
            final LoanTransaction loanRepaymentTransaction = loanAccountDomainService.makeRepayment(LoanTransactionType.REPAYMENT,
                    toLoanAccount, request.getTransferDate(), request.getTransferAmount(), paymentDetail, null, externalId,
                    isRecoveryRepayment, chargeRefundChargeType, isAccountTransfer, holidayDetailDto, isHolidayValidationDone);
            toLoanAccount = loanRepaymentTransaction.getLoan();

            final AccountTransferDetails accountTransferDetails = accountTransfersAssembler.assembleSavingsToLoanTransfer(command,
                    fromSavingsAccount, toLoanAccount, withdrawal, loanRepaymentTransaction);

            accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
            transferDetailId = accountTransferDetails.getId();

        } else if (isLoanToSavingsAccountTransfer(fromAccountType, toAccountType)) {
            // FIXME - kw - ADD overpaid loan to savings account transfer
            // support.

            fromLoanAccountId = request.getFromAccountId();
            final Loan fromLoanAccount = loanAccountAssembler.assembleFrom(fromLoanAccountId);
            final ExternalId externalId = externalIdFactory.create();
            final LoanTransaction loanRefundTransaction = loanAccountDomainService.makeRefund(fromLoanAccountId,
                    new CommandProcessingResultBuilder(), request.getTransferDate(), request.getTransferAmount(), paymentDetail, null,
                    externalId);

            final SavingsAccount toSavingsAccount = savingsAccountAssembler.assembleFrom(request.getToAccountId(),
                    backdatedTxnsAllowedTill);

            final SavingsAccountTransaction deposit = savingsAccountDomainService.handleDeposit(toSavingsAccount, fmt,
                    request.getTransferDate(), request.getTransferAmount(), paymentDetail, isAccountTransfer, isRegularTransaction,
                    backdatedTxnsAllowedTill);

            final AccountTransferDetails accountTransferDetails = accountTransfersAssembler.assembleLoanToSavingsTransfer(command,
                    fromLoanAccount, toSavingsAccount, deposit, loanRefundTransaction);

            accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
            transferDetailId = accountTransferDetails.getId();
        }

        final CommandProcessingResultBuilder builder = new CommandProcessingResultBuilder().withEntityId(transferDetailId);

        if (fromAccountType.isSavingsAccount()) {
            builder.withSavingsId(fromSavingsAccountId);
        }
        if (fromAccountType.isLoanAccount()) {
            builder.withLoanId(fromLoanAccountId);
        }

        return AccountTransferResponse.builder().savingsId(fromSavingsAccountId).loanId(fromLoanAccountId).resourceId(transferDetailId)
                .build();
    }

    @Transactional
    @Override
    public void reverseTransfersWithFromAccountType(Long accountNumber, PortfolioAccountType accountTypeId) {
        if (accountTypeId.isLoanAccount()) {
            final List<AccountTransferTransaction> accountTransfers = List
                    .copyOf(accountTransferRepository.findByFromLoanId(accountNumber));
            if (!accountTransfers.isEmpty()) {
                undoTransactions(accountTransfers);
            }
        }
    }

    @Transactional
    @Override
    public Long transferFunds(AccountTransferDTO accountTransferDTO) {
        // Long transferTransactionId = null;
        final boolean isAccountTransfer = true;
        final boolean isRegularTransaction = accountTransferDTO.isRegularTransaction();
        final boolean backdatedTxnsAllowedTill = false;
        final AccountTransferDetails accountTransferDetails = accountTransferDTO.getAccountTransferDetails();

        if (isSavingsToLoanAccountTransfer(accountTransferDTO.getFromAccountType(), accountTransferDTO.getToAccountType())) {
            return savingsToLoanAccountTransfer(accountTransferDTO, accountTransferDetails, isAccountTransfer, isRegularTransaction,
                    backdatedTxnsAllowedTill);
        } else if (isSavingsToSavingsAccountTransfer(accountTransferDTO.getFromAccountType(), accountTransferDTO.getToAccountType())) {
            return savingsToSavingsAccountTransfer(accountTransferDTO, accountTransferDetails, isAccountTransfer, isRegularTransaction,
                    backdatedTxnsAllowedTill);
        } else if (isLoanToSavingsAccountTransfer(accountTransferDTO.getFromAccountType(), accountTransferDTO.getToAccountType())) {
            return loanToSavingsAccountTransfer(accountTransferDTO, accountTransferDetails, isAccountTransfer, isRegularTransaction,
                    backdatedTxnsAllowedTill);
        } else {
            throw new GeneralPlatformDomainRuleException("error.msg.accounttransfer.loan.to.loan.not.supported",
                    "Account transfer from loan to another loan is not supported");
        }
    }

    private Long loanToSavingsAccountTransfer(AccountTransferDTO accountTransferDTO, AccountTransferDetails accountTransferDetails,
            boolean isAccountTransfer, boolean isRegularTransaction, boolean backdatedTxnsAllowedTill) {
        Loan fromLoanAccount = null;
        SavingsAccount toSavingsAccount = null;
        if (accountTransferDetails == null) {
            if (accountTransferDTO.getLoan() == null) {
                fromLoanAccount = loanAccountAssembler.assembleFrom(accountTransferDTO.getFromAccountId());
            } else {
                fromLoanAccount = accountTransferDTO.getLoan();
            }
            toSavingsAccount = savingsAccountAssembler.assembleFrom(accountTransferDTO.getToAccountId(), backdatedTxnsAllowedTill);
        } else {
            fromLoanAccount = accountTransferDetails.fromLoanAccount();
            toSavingsAccount = accountTransferDetails.toSavingsAccount();
            savingsAccountAssembler.setHelpers(toSavingsAccount);
        }
        LoanTransaction loanTransaction = null;

        ExternalId txnExternalId = accountTransferDTO.getTxnExternalId();
        // Safety net (it might need to generate new one)
        ExternalId externalId = externalIdFactory.create(txnExternalId.getValue());

        if (LoanTransactionType.DISBURSEMENT.getValue().equals(accountTransferDTO.getFromTransferType())) {
            loanTransaction = loanAccountDomainService.makeDisburseTransaction(accountTransferDTO.getFromAccountId(),
                    accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                    accountTransferDTO.getPaymentDetail(), accountTransferDTO.getNoteText(), externalId);
        } else {
            loanTransaction = loanAccountDomainService.makeRefund(accountTransferDTO.getFromAccountId(),
                    new CommandProcessingResultBuilder(), accountTransferDTO.getTransactionDate(),
                    accountTransferDTO.getTransactionAmount(), accountTransferDTO.getPaymentDetail(), accountTransferDTO.getNoteText(),
                    externalId);
        }

        final SavingsAccountTransaction deposit = savingsAccountDomainService.handleDeposit(toSavingsAccount, accountTransferDTO.getFmt(),
                accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(), accountTransferDTO.getPaymentDetail(),
                isAccountTransfer, isRegularTransaction, backdatedTxnsAllowedTill);
        accountTransferDetails = accountTransferAssembler.assembleLoanToSavingsTransfer(accountTransferDTO, fromLoanAccount,
                toSavingsAccount, deposit, loanTransaction);
        accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
        // transferTransactionId = accountTransferDetails.getId();

        // if the savings account is GSIM, update its parent as well
        if (toSavingsAccount.getGsim() != null) {
            GroupSavingsIndividualMonitoring gsim = gsimRepository.findById(toSavingsAccount.getGsim().getId()).orElseThrow();
            BigDecimal currentBalance = gsim.getParentDeposit();
            BigDecimal newBalance = currentBalance.add(accountTransferDTO.getTransactionAmount());
            gsim.setParentDeposit(newBalance);
            gsimRepository.save(gsim);
        }
        return accountTransferDetails.getId();
    }

    private Long savingsToSavingsAccountTransfer(AccountTransferDTO accountTransferDTO, AccountTransferDetails accountTransferDetails,
            boolean isAccountTransfer, boolean isRegularTransaction, boolean backdatedTxnsAllowedTill) {
        SavingsAccount fromSavingsAccount = null;
        SavingsAccount toSavingsAccount = null;
        if (accountTransferDetails == null) {
            if (accountTransferDTO.getFromSavingsAccount() == null) {
                fromSavingsAccount = savingsAccountAssembler.assembleFrom(accountTransferDTO.getFromAccountId(), backdatedTxnsAllowedTill);
            } else {
                fromSavingsAccount = accountTransferDTO.getFromSavingsAccount();
                savingsAccountAssembler.setHelpers(fromSavingsAccount);
            }
            if (accountTransferDTO.getToSavingsAccount() == null) {
                toSavingsAccount = savingsAccountAssembler.assembleFrom(accountTransferDTO.getToAccountId(), false);
            } else {
                toSavingsAccount = accountTransferDTO.getToSavingsAccount();
                savingsAccountAssembler.setHelpers(toSavingsAccount);
            }
        } else {
            fromSavingsAccount = accountTransferDetails.fromSavingsAccount();
            savingsAccountAssembler.setHelpers(fromSavingsAccount);
            toSavingsAccount = accountTransferDetails.toSavingsAccount();
            savingsAccountAssembler.setHelpers(toSavingsAccount);
        }

        final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
                isRegularTransaction, fromSavingsAccount.isWithdrawalFeeApplicableForTransfer(),
                AccountTransferType.fromInt(accountTransferDTO.getTransferType()).isInterestTransfer(),
                accountTransferDTO.isExceptionForBalanceCheck());

        LocalDate transactionDate = accountTransferDTO.getTransactionDate();
        if (configurationDomainService.isSavingsInterestPostingAtCurrentPeriodEnd()
                && configurationDomainService.isNextDayFixedDepositInterestTransferEnabledForPeriodEnd()
                && AccountTransferType.fromInt(accountTransferDTO.getTransferType()).isInterestTransfer()) {
            transactionDate = transactionDate.plusDays(DAYS_TO_ADD);
        }

        final SavingsAccountTransaction withdrawal = savingsAccountDomainService.handleWithdrawal(fromSavingsAccount,
                accountTransferDTO.getFmt(), transactionDate, accountTransferDTO.getTransactionAmount(),
                accountTransferDTO.getPaymentDetail(), transactionBooleanValues, backdatedTxnsAllowedTill);

        final SavingsAccountTransaction deposit = savingsAccountDomainService.handleDeposit(toSavingsAccount, accountTransferDTO.getFmt(),
                transactionDate, accountTransferDTO.getTransactionAmount(), accountTransferDTO.getPaymentDetail(), isAccountTransfer,
                isRegularTransaction, backdatedTxnsAllowedTill);

        accountTransferDetails = accountTransferAssembler.assembleSavingsToSavingsTransfer(accountTransferDTO, fromSavingsAccount,
                toSavingsAccount, withdrawal, deposit);
        accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
        return accountTransferDetails.getId();
    }

    private Long savingsToLoanAccountTransfer(AccountTransferDTO accountTransferDTO, AccountTransferDetails accountTransferDetails,
            boolean isAccountTransfer, boolean isRegularTransaction, boolean backdatedTxnsAllowedTill) {
        SavingsAccount fromSavingsAccount = null;
        Loan toLoanAccount = null;
        if (accountTransferDetails == null) {
            if (accountTransferDTO.getFromSavingsAccount() == null) {
                fromSavingsAccount = savingsAccountAssembler.assembleFrom(accountTransferDTO.getFromAccountId(), backdatedTxnsAllowedTill);
            } else {
                fromSavingsAccount = accountTransferDTO.getFromSavingsAccount();
                savingsAccountAssembler.setHelpers(fromSavingsAccount);
            }
            if (accountTransferDTO.getLoan() == null) {
                toLoanAccount = loanAccountAssembler.assembleFrom(accountTransferDTO.getToAccountId());
            } else {
                toLoanAccount = accountTransferDTO.getLoan();
            }

        } else {
            fromSavingsAccount = accountTransferDetails.fromSavingsAccount();
            savingsAccountAssembler.setHelpers(fromSavingsAccount);
            toLoanAccount = accountTransferDetails.toLoanAccount();
        }

        final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
                isRegularTransaction, fromSavingsAccount.isWithdrawalFeeApplicableForTransfer(),
                AccountTransferType.fromInt(accountTransferDTO.getTransferType()).isInterestTransfer(),
                accountTransferDTO.isExceptionForBalanceCheck());

        final SavingsAccountTransaction withdrawal = savingsAccountDomainService.handleWithdrawal(fromSavingsAccount,
                accountTransferDTO.getFmt(), accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                accountTransferDTO.getPaymentDetail(), transactionBooleanValues, backdatedTxnsAllowedTill);

        LoanTransaction loanTransaction;

        ExternalId txnExternalId = accountTransferDTO.getTxnExternalId();
        // Safety net (it might need to generate new one)
        ExternalId externalId = externalIdFactory.create(txnExternalId.getValue());

        if (AccountTransferType.fromInt(accountTransferDTO.getTransferType()).isChargePayment()) {
            loanTransaction = loanAccountDomainService.makeChargePayment(toLoanAccount, accountTransferDTO.getChargeId(),
                    accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                    accountTransferDTO.getPaymentDetail(), null, externalId, accountTransferDTO.getToTransferType(),
                    accountTransferDTO.getLoanInstallmentNumber());

        } else if (AccountTransferType.fromInt(accountTransferDTO.getTransferType()).isLoanDownPayment()) {
            final boolean isRecoveryRepayment = false;
            final Boolean isHolidayValidationDone = false;
            final HolidayDetailDTO holidayDetailDto = null;
            final String chargeRefundChargeType = null;
            loanTransaction = loanAccountDomainService.makeRepayment(LoanTransactionType.DOWN_PAYMENT, toLoanAccount,
                    accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                    accountTransferDTO.getPaymentDetail(), null, externalId, isRecoveryRepayment, chargeRefundChargeType, isAccountTransfer,
                    holidayDetailDto, isHolidayValidationDone);
            toLoanAccount = loanTransaction.getLoan();
        } else {
            final boolean isRecoveryRepayment = false;
            final boolean isHolidayValidationDone = false;
            final HolidayDetailDTO holidayDetailDto = null;
            final String chargeRefundChargeType = null;
            loanTransaction = loanAccountDomainService.makeRepayment(LoanTransactionType.REPAYMENT, toLoanAccount,
                    accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                    accountTransferDTO.getPaymentDetail(), null, externalId, isRecoveryRepayment, chargeRefundChargeType, isAccountTransfer,
                    holidayDetailDto, isHolidayValidationDone);
            toLoanAccount = loanTransaction.getLoan();
        }

        accountTransferDetails = accountTransferAssembler.assembleSavingsToLoanTransfer(accountTransferDTO, fromSavingsAccount,
                toLoanAccount, withdrawal, loanTransaction);
        accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
        return accountTransferDetails.getId();
    }

    @Transactional
    @Override
    public void reverseAllTransactions(Long accountId, PortfolioAccountType accountTypeId) {
        if (accountTypeId.isLoanAccount()) {
            List<AccountTransferTransaction> accountTransfers = accountTransferRepository.findAllByLoanId(accountId);
            if (!accountTransfers.isEmpty()) {
                undoTransactions(accountTransfers);
            }
        }
    }

    @Transactional
    @Override
    public void reverseTransfersWithFromAccountTransactions(List<Long> fromTransactionIds, PortfolioAccountType accountTypeId) {
        List<AccountTransferTransaction> accountTransfers = new ArrayList<>();
        if (accountTypeId.isLoanAccount()) {
            List<List<Long>> partitions = Lists.partition(fromTransactionIds.stream().toList(),
                    fineractProperties.getQuery().getInClauseParameterSizeLimit());
            partitions.forEach(partition -> accountTransfers.addAll(accountTransferRepository.findByFromLoanTransactions(partition)));
        }
        if (!accountTransfers.isEmpty()) {
            undoTransactions(accountTransfers);
        }
    }

    @Override
    public AccountTransferDetails repayLoanWithTopup(AccountTransferDTO accountTransferDTO) {
        final boolean isAccountTransfer = true;
        Loan fromLoanAccount = null;
        if (accountTransferDTO.getFromLoan() == null) {
            fromLoanAccount = loanAccountAssembler.assembleFrom(accountTransferDTO.getFromAccountId());
        } else {
            fromLoanAccount = accountTransferDTO.getFromLoan();
        }
        Loan toLoanAccount = null;
        if (accountTransferDTO.getToLoan() == null) {
            toLoanAccount = loanAccountAssembler.assembleFrom(accountTransferDTO.getToAccountId());
        } else {
            toLoanAccount = accountTransferDTO.getToLoan();
        }

        ExternalId externalIdForDisbursement = accountTransferDTO.getTxnExternalId();

        LoanTransaction disburseTransaction = loanAccountDomainService.makeDisburseTransaction(accountTransferDTO.getFromAccountId(),
                accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(), accountTransferDTO.getPaymentDetail(),
                accountTransferDTO.getNoteText(), externalIdForDisbursement, true);
        final String chargeRefundChargeType = null;

        ExternalId externalIdForRepayment = externalIdFactory.create();

        LoanTransaction repayTransaction = loanAccountDomainService.makeRepayment(LoanTransactionType.REPAYMENT, toLoanAccount,
                accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(), accountTransferDTO.getPaymentDetail(),
                null, externalIdForRepayment, false, chargeRefundChargeType, isAccountTransfer, null, false, true);

        AccountTransferDetails accountTransferDetails = accountTransferAssembler.assembleLoanToLoanTransfer(accountTransferDTO,
                fromLoanAccount, toLoanAccount, disburseTransaction, repayTransaction);
        accountTransferDetailRepository.saveAndFlush(accountTransferDetails);

        return accountTransferDetails;
    }

    @Transactional
    @Override
    public RefundByTransferResponse refundByTransfer(Command<RefundByTransferRequest> command) {

        final RefundByTransferRequest request = command.getPayload();
        final LocalDate transactionDate = request.getTransferDate();
        final BigDecimal transactionAmount = request.getTransferAmount();
        final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(Locale.of("en"));

        final PaymentDetail paymentDetail = null;
        Long transferTransactionId = null;

        final Long fromLoanAccountId = request.getFromAccountId();
        final Loan fromLoanAccount = loanAccountAssembler.assembleFrom(fromLoanAccountId);

        BigDecimal overpaid = loanReadPlatformService.retrieveTotalPaidInAdvance(fromLoanAccountId).getPaidInAdvance();
        final boolean backdatedTxnsAllowedTill = false;

        if (overpaid == null || overpaid.compareTo(BigDecimal.ZERO) == 0 || transactionAmount.floatValue() > overpaid.floatValue()) {
            if (overpaid == null) {
                overpaid = BigDecimal.ZERO;
            }
            throw new InvalidPaidInAdvanceAmountException(overpaid.toPlainString());
        }

        final ExternalId externalId = externalIdFactory.create();

        final LoanTransaction loanRefundTransaction = loanAccountDomainService.makeRefundForActiveLoan(fromLoanAccountId,
                new CommandProcessingResultBuilder(), transactionDate, transactionAmount, paymentDetail, null, externalId);

        final Long toSavingsAccountId = request.getToAccountId();
        final SavingsAccount toSavingsAccount = savingsAccountAssembler.assembleFrom(toSavingsAccountId, backdatedTxnsAllowedTill);

        final SavingsAccountTransaction deposit = savingsAccountDomainService.handleDeposit(toSavingsAccount, fmt, transactionDate,
                transactionAmount, paymentDetail, true, true, backdatedTxnsAllowedTill);

        final AccountTransferDetails accountTransferDetails = accountTransfersAssembler.assembleLoanToSavingsTransferRefund(command,
                fromLoanAccount, toSavingsAccount, deposit, loanRefundTransaction);
        accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
        transferTransactionId = accountTransferDetails.getId();

        return RefundByTransferResponse.builder().savingsId(toSavingsAccountId).resourceId(transferTransactionId).build();
    }

    private void undoTransactions(final List<AccountTransferTransaction> accountTransfers) {
        for (final AccountTransferTransaction accountTransfer : accountTransfers) {
            if (accountTransfer.getFromLoanTransaction() != null) {
                loanAccountDomainService.reverseTransfer(accountTransfer.getFromLoanTransaction());
            }
            if (accountTransfer.getToLoanTransaction() != null) {
                loanAccountDomainService.reverseTransfer(accountTransfer.getToLoanTransaction());
            }
            if (accountTransfer.getFromTransaction() != null) {
                savingsAccountWritePlatformService.undoTransaction(accountTransfer.accountTransferDetails().fromSavingsAccount().getId(),
                        accountTransfer.getFromTransaction().getId(), true);
            }
            if (accountTransfer.getToSavingsTransaction() != null) {
                savingsAccountWritePlatformService.undoTransaction(accountTransfer.accountTransferDetails().toSavingsAccount().getId(),
                        accountTransfer.getToSavingsTransaction().getId(), true);
            }
            accountTransfer.reverse();
            accountTransferRepository.save(accountTransfer);
        }
    }

    private boolean isLoanToSavingsAccountTransfer(final PortfolioAccountType fromAccountType, final PortfolioAccountType toAccountType) {
        return fromAccountType == PortfolioAccountType.LOAN && toAccountType == PortfolioAccountType.SAVINGS;
    }

    private boolean isSavingsToLoanAccountTransfer(final PortfolioAccountType fromAccountType, final PortfolioAccountType toAccountType) {
        return fromAccountType == PortfolioAccountType.SAVINGS && toAccountType == PortfolioAccountType.LOAN;
    }

    private boolean isSavingsToSavingsAccountTransfer(final PortfolioAccountType fromAccountType,
            final PortfolioAccountType toAccountType) {
        return fromAccountType == PortfolioAccountType.SAVINGS && toAccountType == PortfolioAccountType.SAVINGS;
    }
}
