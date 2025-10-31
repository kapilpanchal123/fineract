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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.command.core.Command;
import org.apache.fineract.infrastructure.core.exception.ErrorHandler;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.portfolio.account.PortfolioAccountType;
import org.apache.fineract.portfolio.account.data.StandingInstructionCreateRequest;
import org.apache.fineract.portfolio.account.data.StandingInstructionCreateResponse;
import org.apache.fineract.portfolio.account.domain.AccountTransferDetailRepository;
import org.apache.fineract.portfolio.account.domain.AccountTransferDetails;
import org.apache.fineract.portfolio.account.domain.StandingInstructionRepository;
import org.apache.fineract.portfolio.account.domain.StandingInstructionsAssembler;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StandingInstructionsWritePlatformServiceImpl implements StandingInstructionsWritePlatformService {

    private final StandingInstructionsAssembler standingInstructionsAssembler;
    private final AccountTransferDetailRepository accountTransferDetailRepository;
    private final StandingInstructionRepository standingInstructionRepository;

    @Transactional
    @Override
    public StandingInstructionCreateResponse create(Command<StandingInstructionCreateRequest> command) {

        final StandingInstructionCreateRequest request = command.getPayload();
        final Integer fromAccountTypeId = request.getFromAccountType();
        final PortfolioAccountType fromAccountType = PortfolioAccountType.fromInt(fromAccountTypeId);
        final Integer toAccountTypeId = request.getToAccountType();
        final PortfolioAccountType toAccountType = PortfolioAccountType.fromInt(toAccountTypeId);
        final Long fromClientId = request.getFromClientId();

        Long standingInstructionId = null;
        try {
            if (isSavingsToSavingsAccountTransfer(fromAccountType, toAccountType)) {
                final AccountTransferDetails standingInstruction = standingInstructionsAssembler.assembleSavingsToSavingsTransfer(command);
                accountTransferDetailRepository.saveAndFlush(standingInstruction);
                standingInstructionId = standingInstruction.accountTransferStandingInstruction().getId();
            } else if (isSavingsToLoanAccountTransfer(fromAccountType, toAccountType)) {
                final AccountTransferDetails standingInstruction = standingInstructionsAssembler.assembleSavingsToLoanTransfer(command);
                accountTransferDetailRepository.saveAndFlush(standingInstruction);
                standingInstructionId = standingInstruction.accountTransferStandingInstruction().getId();
            } else if (isLoanToSavingsAccountTransfer(fromAccountType, toAccountType)) {
                final AccountTransferDetails standingInstruction = standingInstructionsAssembler.assembleLoanToSavingsTransfer(command);
                accountTransferDetailRepository.saveAndFlush(standingInstruction);
                standingInstructionId = standingInstruction.accountTransferStandingInstruction().getId();
            }
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            final Throwable throwable = dve.getMostSpecificCause();
            handleDataIntegrityIssues(command, throwable, dve);
            return new StandingInstructionCreateResponse();
        }
        // final CommandProcessingResultBuilder builder = new
        // CommandProcessingResultBuilder().withEntityId(standingInstructionId)
        // .withClientId(fromClientId);
        // return builder.build();

        return StandingInstructionCreateResponse.builder().clientId(fromClientId).resourceId(standingInstructionId).build();
    }

    private void handleDataIntegrityIssues(final Command<StandingInstructionCreateRequest> command, Throwable realCause,
            final NonTransientDataAccessException dve) {
        StandingInstructionCreateRequest request = command.getPayload();
        if (realCause.getMessage().contains("name")) {
            final String name = request.getName();
            throw new PlatformDataIntegrityException("error.msg.standinginstruction.duplicate.name",
                    "Standinginstruction with name `" + name + "` already exists", "name", name);
        }
        log.error("Error occured.", dve);
        throw ErrorHandler.getMappable(dve, "error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
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
