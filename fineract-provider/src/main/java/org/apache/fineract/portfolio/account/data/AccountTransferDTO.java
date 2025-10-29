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
package org.apache.fineract.portfolio.account.data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.fineract.infrastructure.core.domain.ExternalId;
import org.apache.fineract.portfolio.account.PortfolioAccountType;
import org.apache.fineract.portfolio.account.domain.AccountTransferDetails;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountTransferDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private LocalDate transactionDate;
    private BigDecimal transactionAmount;
    private PortfolioAccountType fromAccountType;
    private PortfolioAccountType toAccountType;
    private Long fromAccountId;
    private Long toAccountId;
    private String description;
    private Locale locale;
    private DateTimeFormatter fmt;
    private PaymentDetail paymentDetail;
    private Integer fromTransferType;
    private Integer toTransferType;
    private Long chargeId;
    private Integer loanInstallmentNumber;
    private Integer transferType;
    private AccountTransferDetails accountTransferDetails;
    private String noteText;
    private ExternalId txnExternalId;
    private Loan loan;
    private Loan fromLoan;
    private Loan toLoan;
    private SavingsAccount toSavingsAccount;
    private SavingsAccount fromSavingsAccount;
    private Boolean isRegularTransaction;
    private Boolean isExceptionForBalanceCheck;

    public AccountTransferDTO(final LocalDate transactionDate, final BigDecimal transactionAmount,
            final PortfolioAccountType fromAccountType, final PortfolioAccountType toAccountType, final Long fromAccountId,
            final Long toAccountId, final String description, final Locale locale, final DateTimeFormatter fmt,
            final PaymentDetail paymentDetail, final Integer fromTransferType, final Integer toTransferType, final Long chargeId,
            Integer loanInstallmentNumber, Integer transferType, final AccountTransferDetails accountTransferDetails, final String noteText,
            final ExternalId txnExternalId, final Loan loan, SavingsAccount toSavingsAccount, final SavingsAccount fromSavingsAccount,
            final Boolean isRegularTransaction, Boolean isExceptionForBalanceCheck) {
        this.transactionDate = transactionDate;
        this.transactionAmount = transactionAmount;
        this.fromAccountType = fromAccountType;
        this.toAccountType = toAccountType;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.description = description;
        this.locale = locale;
        this.fmt = fmt;
        this.paymentDetail = paymentDetail;
        this.fromTransferType = fromTransferType;
        this.toTransferType = toTransferType;
        this.chargeId = chargeId;
        this.loanInstallmentNumber = loanInstallmentNumber;
        this.transferType = transferType;
        this.accountTransferDetails = accountTransferDetails;
        this.noteText = noteText;
        this.txnExternalId = txnExternalId;
        this.loan = loan;
        this.toSavingsAccount = toSavingsAccount;
        this.fromSavingsAccount = fromSavingsAccount;
        this.isRegularTransaction = isRegularTransaction;
        this.isExceptionForBalanceCheck = isExceptionForBalanceCheck;
    }

    public AccountTransferDTO(final LocalDate transactionDate, final BigDecimal transactionAmount,
            final PortfolioAccountType fromAccountType, final PortfolioAccountType toAccountType, final Long fromAccountId,
            final Long toAccountId, final String description, final Locale locale, final DateTimeFormatter fmt,
            final Integer fromTransferType, final Integer toTransferType, final ExternalId txnExternalId, final Loan fromLoan,
            final Loan toLoan) {
        this.transactionDate = transactionDate;
        this.transactionAmount = transactionAmount;
        this.fromAccountType = fromAccountType;
        this.toAccountType = toAccountType;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.description = description;
        this.locale = locale;
        this.fmt = fmt;
        this.fromTransferType = fromTransferType;
        this.toTransferType = toTransferType;
        this.txnExternalId = txnExternalId;
        this.fromLoan = fromLoan;
        this.toLoan = toLoan;
    }

    public Boolean isRegularTransaction() {
        return this.isRegularTransaction;
    }

    public Boolean isExceptionForBalanceCheck() {
        return this.isExceptionForBalanceCheck;
    }
}
