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
public enum LoanPreCloseInterestCalculationStrategyENUM implements Serializable {

  NONE(0, "loanPreClosureInterestCalculationStrategy.none"), //
  TILL_PRE_CLOSURE_DATE(1, "loanPreClosureInterestCalculationStrategy.tillPreClosureDate"), //
  TILL_REST_FREQUENCY_DATE(2, "loanPreClosureInterestCalculationStrategy.tillRestFrequencyDate");

  @Getter
  private final Integer value;

  @Getter
  private final String code;

  private static final Map<Integer, LoanPreCloseInterestCalculationStrategyENUM> intToEnumMap = new HashMap<>();
  private static int minValue;
  private static int maxValue;

  static {
    int i = 0;
    for (final LoanPreCloseInterestCalculationStrategyENUM type : LoanPreCloseInterestCalculationStrategyENUM.values()) {
      if (i == 0) {
        minValue = type.value;
      }
      intToEnumMap.put(type.value, type);
      if (minValue >= type.value) {
        minValue = type.value;
      }
      if (maxValue < type.value) {
        maxValue = type.value;
      }
      i++;
    }
  }

  public static LoanPreCloseInterestCalculationStrategyENUM fromInt(final Integer ruleTypeValue) {
    return intToEnumMap.get(ruleTypeValue);
  }
}
