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
public class SavingsAccountTransactionData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private SavingsAccountTransactionEnumData transactionType;
    private TransactionEntryType entryType;

    private Long accountId;
    private String accountNo;
    private LocalDate date;
    private CurrencyData currency;
    private PaymentDetailData paymentDetailData;
    private BigDecimal amount;
    private BigDecimal outstandingChargeAmount;
    private BigDecimal runningBalance;
    private boolean reversed;
    private AccountTransferData transfer;
    private LocalDate submittedOnDate;
    private boolean interestedPostedAsOn;
    private String submittedByUsername;
    private String note;
    private boolean isManualTransaction;
    private Boolean isReversal;
    private Long originalTransactionId;
    private Boolean lienTransaction;
    private Long releaseTransactionId;
    private String reasonForBlock;
    private Set<SavingsAccountChargesPaidByData> chargesPaidByData;

    // templates
    private List<PaymentTypeData> paymentTypeOptions;

    // import fields
    private transient Integer rowIndex;
    private transient Long savingsAccountId;
    private String dateFormat;
    private String locale;
    private LocalDate transactionDate;
    private BigDecimal transactionAmount;
    private Long paymentTypeId;
    private String accountNumber;
    private String checkNumber;
    private String routingCode;
    private String receiptNumber;
    private String bankNumber;

    private BigDecimal cumulativeBalance;
    private LocalDate balanceEndDate;
    private transient List<TaxDetailsData> taxDetails;
    private Integer balanceNumberOfDays;
    private BigDecimal overdraftAmount;
    private transient Long modifiedId;
    private transient String refNo;
    private Boolean isOverdraft;

    private Long accountCredit;
    private Long accountDebit;
}
