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

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PaymentAllocationType {

    PAST_DUE_PENALTY(DueType.PAST_DUE, AllocationType.PENALTY, "Past due penalty"), PAST_DUE_FEE(DueType.PAST_DUE, AllocationType.FEE,
            "Past due fee"), PAST_DUE_PRINCIPAL(DueType.PAST_DUE, AllocationType.PRINCIPAL, "Past due principal"), PAST_DUE_INTEREST(
                    DueType.PAST_DUE, AllocationType.INTEREST,
                    "Past due interest"), DUE_PENALTY(DueType.DUE, AllocationType.PENALTY, "Due penalty"), DUE_FEE(DueType.DUE,
                            AllocationType.FEE,
                            "Due fee"), DUE_PRINCIPAL(DueType.DUE, AllocationType.PRINCIPAL, "Due principal"), DUE_INTEREST(DueType.DUE,
                                    AllocationType.INTEREST, "Due interest"), IN_ADVANCE_PENALTY(DueType.IN_ADVANCE, AllocationType.PENALTY,
                                            "In advance penalty"), IN_ADVANCE_FEE(DueType.IN_ADVANCE, AllocationType.FEE,
                                                    "In advance fee"), IN_ADVANCE_PRINCIPAL(DueType.IN_ADVANCE, AllocationType.PRINCIPAL,
                                                            "In advance principal"), IN_ADVANCE_INTEREST(DueType.IN_ADVANCE,
                                                                    AllocationType.INTEREST, "In advanced interest");

    @Getter
    private final DueType dueType;

    @Getter
    private final AllocationType allocationType;

    @Getter
    private final String humanReadableName;
}
