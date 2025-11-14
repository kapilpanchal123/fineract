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
public enum DaysInYearTypeENUM implements Serializable {

  INVALID(0, "DaysInYearType.invalid"), //
  ACTUAL(1, "DaysInYearType.actual"), //
  DAYS_360(360, "DaysInYearType.days360"), //
  DAYS_364(364, "DaysInYearType.days364"), //
  DAYS_365(365, "DaysInYearType.days365");

  @Getter
  private final Integer value;

  @Getter
  private final String code;

  public static DaysInYearTypeENUM fromInt(final Integer type) {
    DaysInYearTypeENUM repaymentFrequencyType = DaysInYearTypeENUM.INVALID;
    if (type != null) {
      repaymentFrequencyType = switch (type) {
        case 1 -> DaysInYearTypeENUM.ACTUAL;
        case 360 -> DaysInYearTypeENUM.DAYS_360;
        case 364 -> DaysInYearTypeENUM.DAYS_364;
        case 365 -> DaysInYearTypeENUM.DAYS_365;
        default -> repaymentFrequencyType;
      };
    }
    return repaymentFrequencyType;
  }
}
