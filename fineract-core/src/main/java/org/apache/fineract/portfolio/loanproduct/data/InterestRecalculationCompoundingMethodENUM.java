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
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum InterestRecalculationCompoundingMethodENUM implements Serializable {

    NONE(0, "interestRecalculationCompoundingMethod.none"), //
    INTEREST(1, "interestRecalculationCompoundingMethod.interest"), //
    FEE(2, "interestRecalculationCompoundingMethod.fee"), //
    INTEREST_AND_FEE(3, "interestRecalculationCompoundingMethod.interest.and.fee"); //

    @Getter
    private final Integer value;

    @Getter
    private final String code;

    private static final Map<Integer, InterestRecalculationCompoundingMethodENUM> intToEnumMap = new HashMap<>();

    static {
        for (final InterestRecalculationCompoundingMethodENUM type : InterestRecalculationCompoundingMethodENUM.values()) {
            intToEnumMap.put(type.value, type);
        }
    }

    public static InterestRecalculationCompoundingMethodENUM fromInt(final Integer ruleTypeValue) {
        return intToEnumMap.get(ruleTypeValue);
    }
}
