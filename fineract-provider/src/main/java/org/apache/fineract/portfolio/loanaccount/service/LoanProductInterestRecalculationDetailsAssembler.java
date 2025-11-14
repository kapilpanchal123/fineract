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
package org.apache.fineract.portfolio.loanaccount.service;

import lombok.RequiredArgsConstructor;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.loanproduct.LoanProductConstants;
import org.apache.fineract.portfolio.loanproduct.data.InterestRecalculationCompoundingMethodENUM;
import org.apache.fineract.portfolio.loanproduct.data.LoanPreCloseInterestCalculationStrategyENUM;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductInterestRecalculationDetailsDTO;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductRequestDTO;
import org.apache.fineract.portfolio.loanproduct.data.LoanRescheduleStrategyMethodENUM;
import org.apache.fineract.portfolio.loanproduct.domain.InterestRecalculationCompoundingMethod;
import org.apache.fineract.portfolio.loanproduct.domain.LoanPreCloseInterestCalculationStrategy;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductInterestRecalculationDetails;
import org.apache.fineract.portfolio.loanproduct.domain.LoanRescheduleStrategyMethod;
import org.apache.fineract.portfolio.loanproduct.domain.RecalculationFrequencyType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanProductInterestRecalculationDetailsAssembler {

    public LoanProductInterestRecalculationDetails createFrom(final JsonCommand command) {
        final Integer interestRecalculationCompoundingMethod = InterestRecalculationCompoundingMethod
                .fromInt(command.integerValueOfParameterNamed(LoanProductConstants.interestRecalculationCompoundingMethodParameterName))
                .getValue();

        final Integer loanRescheduleStrategyMethod = LoanRescheduleStrategyMethod
                .fromInt(command.integerValueOfParameterNamed(LoanProductConstants.rescheduleStrategyMethodParameterName)).getValue();

        final Integer recurrenceFrequency = command
                .integerValueOfParameterNamed(LoanProductConstants.recalculationRestFrequencyTypeParameterName);
        final Integer recurrenceOnNthDay = command
                .integerValueOfParameterNamed(LoanProductConstants.recalculationRestFrequencyNthDayParamName);
        final Integer recurrenceOnDay = command.integerValueOfParameterNamed(LoanProductConstants.recalculationRestFrequencyOnDayParamName);
        final Integer recurrenceOnWeekday = command
                .integerValueOfParameterNamed(LoanProductConstants.recalculationRestFrequencyWeekdayParamName);
        Integer recurrenceInterval = command
                .integerValueOfParameterNamed(LoanProductConstants.recalculationRestFrequencyIntervalParameterName);
        final boolean isArrearsBasedOnOriginalSchedule = command
                .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.isArrearsBasedOnOriginalScheduleParamName);
        RecalculationFrequencyType frequencyType = RecalculationFrequencyType.fromInt(recurrenceFrequency);
        if (frequencyType.isSameAsRepayment()) {
            recurrenceInterval = 0;
        }

        InterestRecalculationCompoundingMethod compoundingMethod = InterestRecalculationCompoundingMethod
                .fromInt(interestRecalculationCompoundingMethod);
        Integer compoundingRecurrenceFrequency = null;
        Integer compoundingInterval = null;
        Integer compoundingRecurrenceOnNthDay = null;
        Integer compoundingRecurrenceOnDay = null;
        Integer compoundingRecurrenceOnWeekday = null;
        boolean allowCompoundingOnEod = false;
        if (compoundingMethod.isCompoundingEnabled()) {
            compoundingRecurrenceFrequency = command
                    .integerValueOfParameterNamed(LoanProductConstants.recalculationCompoundingFrequencyTypeParameterName);
            compoundingInterval = command
                    .integerValueOfParameterNamed(LoanProductConstants.recalculationCompoundingFrequencyIntervalParameterName);
            RecalculationFrequencyType compoundingFrequencyType = RecalculationFrequencyType.fromInt(compoundingRecurrenceFrequency);
            if (compoundingFrequencyType.isSameAsRepayment()) {
                recurrenceInterval = 0;
            }
            compoundingRecurrenceOnNthDay = command
                    .integerValueOfParameterNamed(LoanProductConstants.recalculationCompoundingFrequencyNthDayParamName);
            compoundingRecurrenceOnDay = command
                    .integerValueOfParameterNamed(LoanProductConstants.recalculationCompoundingFrequencyOnDayParamName);
            compoundingRecurrenceOnWeekday = command
                    .integerValueOfParameterNamed(LoanProductConstants.recalculationCompoundingFrequencyWeekdayParamName);
            if (!compoundingFrequencyType.isDaily()) {
                allowCompoundingOnEod = command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.allowCompoundingOnEodParamName);
            }
        }

        Integer preCloseInterestCalculationStrategy = command
                .integerValueOfParameterNamed(LoanProductConstants.preClosureInterestCalculationStrategyParamName);
        if (preCloseInterestCalculationStrategy == null) {
            preCloseInterestCalculationStrategy = LoanPreCloseInterestCalculationStrategy.TILL_PRE_CLOSURE_DATE.getValue();
        }

        final boolean isCompoundingToBePostedAsTransaction = command
                .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.isCompoundingToBePostedAsTransactionParamName);

        final boolean disallowInterestCalculationOnPastDue = command
                .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.disallowInterestCalculationOnPastDueParamName);

        return new LoanProductInterestRecalculationDetails(interestRecalculationCompoundingMethod, loanRescheduleStrategyMethod,
                recurrenceFrequency, recurrenceInterval, recurrenceOnNthDay, recurrenceOnDay, recurrenceOnWeekday,
                compoundingRecurrenceFrequency, compoundingInterval, compoundingRecurrenceOnNthDay, compoundingRecurrenceOnDay,
                compoundingRecurrenceOnWeekday, isArrearsBasedOnOriginalSchedule, preCloseInterestCalculationStrategy,
                isCompoundingToBePostedAsTransaction, allowCompoundingOnEod, disallowInterestCalculationOnPastDue);
    }

    public LoanProductInterestRecalculationDetailsDTO createFromDTO(final LoanProductRequestDTO loanProductRequestDTO) {
//      final Integer interestRecalculationCompoundingMethod = InterestRecalculationCompoundingMethod
//          .fromInt(command.integerValueOfParameterNamed(LoanProductConstants.interestRecalculationCompoundingMethodParameterName))
//          .getValue();
      final Integer interestRecalculationCompoundingMethod = InterestRecalculationCompoundingMethodENUM
          .fromInt(loanProductRequestDTO.getInterestRecalculationData().getInterestRecalculationCompoundingType().getId().intValue())
          .getValue();

//      final Integer loanRescheduleStrategyMethod = LoanRescheduleStrategyMethod
//          .fromInt(command.integerValueOfParameterNamed(LoanProductConstants.rescheduleStrategyMethodParameterName)).getValue();
      final Integer loanRescheduleStrategyMethod = LoanRescheduleStrategyMethodENUM
          .fromInt(loanProductRequestDTO.getAllowAttributeOverrides().getLoanProduct().getProductInterestRecalculationDetails().getRescheduleStrategyMethod()).getValue();

//      final Integer recurrenceFrequency = command
//          .integerValueOfParameterNamed(LoanProductConstants.recalculationRestFrequencyTypeParameterName);
      final Integer recurrenceFrequency = loanProductRequestDTO.getInterestRecalculationData().getRecalculationRestFrequencyType().getId().intValue();

//      final Integer recurrenceOnNthDay = command
//          .integerValueOfParameterNamed(LoanProductConstants.recalculationRestFrequencyNthDayParamName);
      final Integer recurrenceOnNthDay = loanProductRequestDTO.getInterestRecalculationData().getRecalculationCompoundingFrequencyNthDay().getId().intValue();

//      final Integer recurrenceOnDay = command.integerValueOfParameterNamed(LoanProductConstants.recalculationRestFrequencyOnDayParamName);
      final Integer recurrenceOnDay = loanProductRequestDTO.getInterestRecalculationData().getRecalculationRestFrequencyOnDay();

//      final Integer recurrenceOnWeekday = command
//          .integerValueOfParameterNamed(LoanProductConstants.recalculationRestFrequencyWeekdayParamName);
      final Integer recurrenceOnWeekday = loanProductRequestDTO.getInterestRecalculationData().getRecalculationRestFrequencyWeekday().getId().intValue();

//      Integer recurrenceInterval = command
//          .integerValueOfParameterNamed(LoanProductConstants.recalculationRestFrequencyIntervalParameterName);
      Integer recurrenceInterval = loanProductRequestDTO.getInterestRecalculationData().getRecalculationRestFrequencyInterval();

//      final boolean isArrearsBasedOnOriginalSchedule = command
//          .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.isArrearsBasedOnOriginalScheduleParamName);
      final boolean isArrearsBasedOnOriginalSchedule = loanProductRequestDTO.getInterestRecalculationData().getIsArrearsBasedOnOriginalSchedule();

      RecalculationFrequencyType frequencyType = RecalculationFrequencyType.fromInt(recurrenceFrequency);
      if (frequencyType.isSameAsRepayment()) {
        recurrenceInterval = 0;
      }

      InterestRecalculationCompoundingMethod compoundingMethod = InterestRecalculationCompoundingMethod
          .fromInt(interestRecalculationCompoundingMethod);
      Integer compoundingRecurrenceFrequency = null;
      Integer compoundingInterval = null;
      Integer compoundingRecurrenceOnNthDay = null;
      Integer compoundingRecurrenceOnDay = null;
      Integer compoundingRecurrenceOnWeekday = null;
      boolean allowCompoundingOnEod = false;

      if (compoundingMethod.isCompoundingEnabled()) {
//        compoundingRecurrenceFrequency = command
//            .integerValueOfParameterNamed(LoanProductConstants.recalculationCompoundingFrequencyTypeParameterName);
        compoundingRecurrenceFrequency = loanProductRequestDTO.getInterestRecalculationData().getRecalculationCompoundingFrequencyType().getId().intValue();

//        compoundingInterval = command
//            .integerValueOfParameterNamed(LoanProductConstants.recalculationCompoundingFrequencyIntervalParameterName);
        compoundingInterval = loanProductRequestDTO.getInterestRecalculationData().getRecalculationCompoundingFrequencyInterval();

        RecalculationFrequencyType compoundingFrequencyType = RecalculationFrequencyType.fromInt(compoundingRecurrenceFrequency);
        if (compoundingFrequencyType.isSameAsRepayment()) {
          recurrenceInterval = 0;
        }
//        compoundingRecurrenceOnNthDay = command
//            .integerValueOfParameterNamed(LoanProductConstants.recalculationCompoundingFrequencyNthDayParamName);
        compoundingRecurrenceOnNthDay = loanProductRequestDTO.getInterestRecalculationData().getRecalculationCompoundingFrequencyNthDay().getId().intValue();

//        compoundingRecurrenceOnDay = command
//            .integerValueOfParameterNamed(LoanProductConstants.recalculationCompoundingFrequencyOnDayParamName);
        compoundingRecurrenceOnDay = loanProductRequestDTO.getInterestRecalculationData().getRecalculationRestFrequencyOnDay();

//        compoundingRecurrenceOnWeekday = command
//            .integerValueOfParameterNamed(LoanProductConstants.recalculationCompoundingFrequencyWeekdayParamName);
        compoundingRecurrenceOnWeekday = loanProductRequestDTO.getInterestRecalculationData().getRecalculationRestFrequencyWeekday().getId().intValue();

        if (!compoundingFrequencyType.isDaily()) {
//          allowCompoundingOnEod = command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.allowCompoundingOnEodParamName);
          allowCompoundingOnEod = loanProductRequestDTO.getInterestRecalculationData().getAllowCompoundingOnEod();
        }
      }

//      Integer preCloseInterestCalculationStrategy = command
//          .integerValueOfParameterNamed(LoanProductConstants.preClosureInterestCalculationStrategyParamName);
      Integer preCloseInterestCalculationStrategy = loanProductRequestDTO.getInterestRecalculationData().getPreClosureInterestCalculationStrategy().getId().intValue();

      if (preCloseInterestCalculationStrategy == null) {
//        preCloseInterestCalculationStrategy = LoanPreCloseInterestCalculationStrategy.TILL_PRE_CLOSURE_DATE.getValue();
        preCloseInterestCalculationStrategy = LoanPreCloseInterestCalculationStrategyENUM.TILL_PRE_CLOSURE_DATE.getValue();
      }

//      final boolean isCompoundingToBePostedAsTransaction = command
//          .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.isCompoundingToBePostedAsTransactionParamName);
      final boolean isCompoundingToBePostedAsTransaction = loanProductRequestDTO.getInterestRecalculationData().getIsCompoundingToBePostedAsTransaction();

//      final boolean disallowInterestCalculationOnPastDue = command
//          .booleanPrimitiveValueOfParameterNamed(LoanProductConstants.disallowInterestCalculationOnPastDueParamName);
      final boolean disallowInterestCalculationOnPastDue = loanProductRequestDTO.getInterestRecalculationData().getDisallowInterestCalculationOnPastDue();

//      return new LoanProductInterestRecalculationDetails(interestRecalculationCompoundingMethod, loanRescheduleStrategyMethod,
//          recurrenceFrequency, recurrenceInterval, recurrenceOnNthDay, recurrenceOnDay, recurrenceOnWeekday,
//          compoundingRecurrenceFrequency, compoundingInterval, compoundingRecurrenceOnNthDay, compoundingRecurrenceOnDay,
//          compoundingRecurrenceOnWeekday, isArrearsBasedOnOriginalSchedule, preCloseInterestCalculationStrategy,
//          isCompoundingToBePostedAsTransaction, allowCompoundingOnEod, disallowInterestCalculationOnPastDue);

      return LoanProductInterestRecalculationDetailsDTO.builder()
          .interestRecalculationCompoundingMethod(interestRecalculationCompoundingMethod)
          .rescheduleStrategyMethod(loanRescheduleStrategyMethod)
          .restFrequencyType(recurrenceFrequency)
          .restInterval(recurrenceInterval)
          .restFrequencyNthDay(recurrenceOnNthDay)
          .restFrequencyOnDay(recurrenceOnDay)
          .restFrequencyWeekday(recurrenceOnWeekday)
          .compoundingInterval(compoundingInterval)
          .compoundingFrequencyNthDay(compoundingRecurrenceOnNthDay)
          .compoundingFrequencyOnDay(compoundingRecurrenceOnDay)
          .compoundingFrequencyWeekday(compoundingRecurrenceOnWeekday)
          .isArrearsBasedOnOriginalSchedule(isArrearsBasedOnOriginalSchedule)
          .preCloseInterestCalculationStrategy(preCloseInterestCalculationStrategy)
          .isCompoundingToBePostedAsTransaction(isCompoundingToBePostedAsTransaction)
          .allowCompoundingOnEod(allowCompoundingOnEod)
          .disallowInterestCalculationOnPastDue(disallowInterestCalculationOnPastDue)
          .build();
    }
}
