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
public enum AccountingRuleTypeENUM implements Serializable {

    NONE(1, "accountingRuleType.none", "No accounting"), //
    CASH_BASED(2, "accountingRuleType.cash", "Cash based accounting"), //
    ACCRUAL_PERIODIC(3, "accountingRuleType.accrual.periodic", "Periodic accrual accounting"), //
    ACCRUAL_UPFRONT(4, "accountingRuleType.accrual.upfront", "Upfront accrual accounting"); //

    @Getter
    private final Integer value;

    @Getter
    private final String code;

    @Getter
    private final String description;
}
