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
public class LoanProductInterestRecalculationDetailsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private LoanProductDTO loanProduct;
    private Integer interestRecalculationCompoundingMethod;
    private Integer rescheduleStrategyMethod;
    private Integer restFrequencyType;
    private Integer restInterval;
    private Integer restFrequencyNthDay;
    private Integer restFrequencyWeekday;
    private Integer restFrequencyOnDay;
    private Integer compoundingFrequencyType;
    private Integer compoundingInterval;
    private Integer compoundingFrequencyNthDay;
    private Integer compoundingFrequencyWeekday;
    private Integer compoundingFrequencyOnDay;
    private Boolean isArrearsBasedOnOriginalSchedule;
    private Integer preCloseInterestCalculationStrategy;
    private Boolean isCompoundingToBePostedAsTransaction;
    private Boolean allowCompoundingOnEod;
    private Boolean disallowInterestCalculationOnPastDue;
}
