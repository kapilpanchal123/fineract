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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.MonthDay;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.command.core.Command;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.account.data.StandingInstructionCreateRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StandingInstructionsAssembler {

    private final AccountTransfersDetailAssembler accountTransfersDetailAssembler;

    public AccountTransferDetails assembleSavingsToSavingsTransfer(final Command<StandingInstructionCreateRequest> command) {
        final AccountTransferDetails accountTransferDetails = accountTransfersDetailAssembler
                .standingInstructionsAssembleSavingsToSavingsTransfer(command);
        assembleStandingInstruction(command, accountTransferDetails);
        return accountTransferDetails;
    }

    public void assembleStandingInstruction(final Command<StandingInstructionCreateRequest> command,
            final AccountTransferDetails accountTransferDetails) {
        final StandingInstructionCreateRequest request = command.getPayload();

        final LocalDate validFrom = request.getValidFrom();
        final LocalDate validTill = request.getValidTill();
        BigDecimal amount = null;
        final BigDecimal transferAmount = request.getAmount();
        if (transferAmount != null) {
            final Money monetaryAmount = Money.of(accountTransferDetails.fromSavingsAccount().getCurrency(), transferAmount);
            amount = monetaryAmount.getAmount();
        }
        final Integer status = request.getStatus();
        final Integer priority = request.getPriority();
        final Integer standingInstructionType = request.getInstructionType();
        final Integer recurrenceType = request.getRecurrenceType();
        final Integer recurrenceFrequency = request.getRecurrenceFrequency();
        // final MonthDay recurrenceOnMonthDay = request.getRecurrenceOnMonthDay();
        final MonthDay recurrenceOnMonthDay = MonthDay.parse("--" + request.getRecurrenceOnMonthDay());
        final Integer recurrenceInterval = request.getRecurrenceInterval();
        final String name = request.getName();
        AccountTransferStandingInstruction accountTransferStandingInstruction = AccountTransferStandingInstruction.create(
                accountTransferDetails, name, priority, standingInstructionType, status, amount, validFrom, validTill, recurrenceType,
                recurrenceFrequency, recurrenceInterval, recurrenceOnMonthDay);
        accountTransferDetails.updateAccountTransferStandingInstruction(accountTransferStandingInstruction);
    }

    public AccountTransferDetails assembleSavingsToLoanTransfer(final Command<StandingInstructionCreateRequest> command) {
        final AccountTransferDetails accountTransferDetails = accountTransfersDetailAssembler
                .standingInstructionsAssembleSavingsToLoanTransfer(command);
        assembleStandingInstruction(command, accountTransferDetails);
        return accountTransferDetails;
    }

    public AccountTransferDetails assembleLoanToSavingsTransfer(final Command<StandingInstructionCreateRequest> command) {
        final AccountTransferDetails accountTransferDetails = accountTransfersDetailAssembler
                .standingInstructionsAssembleLoanToSavingsTransfer(command);
        assembleStandingInstruction(command, accountTransferDetails);
        return accountTransferDetails;
    }
}
