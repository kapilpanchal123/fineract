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
import java.math.BigDecimal;
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
public class ChargeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String CHARGE_TIME_PARAM_NAME = "chargeTimeType";
    public static final String CHARGE_CALCULATION_TYPE_PARAM_NAME = "chargeCalculationType";
    public static final String FEE_ON_MONTH_DAY_PARAM_NAME = "feeOnMonthDay";
    public static final String FEE_INTERVAL_PARAM_NAME = "feeInterval";
    public static final String LOCALE_PARAM_NAME = "locale";
    public static final String FEE_FREQUENCY_PARAM_NAME = "feeFrequency";

    private String name;
    private BigDecimal amount;
    private String currencyCode;
    private Integer chargeAppliesTo;
    private Integer chargeTimeType;
    private Integer chargeCalculation;
    private Integer chargePaymentMode;
    private Integer feeOnDay;
    private Integer feeInterval;
    private Integer feeOnMonth;
    private Boolean penalty;
    private Boolean active;
    private Boolean deleted = false;
    private BigDecimal minCap;
    private BigDecimal maxCap;
    private Integer feeFrequency;
    private Boolean enableFreeWithdrawal;
    private Integer freeWithdrawalFrequency;
    private Integer restartFrequency;
    private Integer restartFrequencyEnum;
    private Boolean enablePaymentType;
    private PaymentTypeDTO paymentType;
    private GLAccountDTO account;
    private TaxGroupDTO taxGroup;
}
