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
public enum DaysInMonthTypeENUM implements Serializable {

    INVALID(0, "DaysInMonthType.invalid"), //
    ACTUAL(1, "DaysInMonthType.actual"), //
    DAYS_30(30, "DaysInMonthType.days360");

    @Getter
    private final Integer value;

    @Getter
    private final String code;

    public static DaysInMonthTypeENUM fromInt(final Integer type) {
        DaysInMonthTypeENUM repaymentFrequencyType = DaysInMonthTypeENUM.INVALID;
        if (type != null) {
            repaymentFrequencyType = switch (type) {
                case 1 -> DaysInMonthTypeENUM.ACTUAL;
                case 30 -> DaysInMonthTypeENUM.DAYS_30;
                default -> repaymentFrequencyType;
            };
        }
        return repaymentFrequencyType;
    }
}
