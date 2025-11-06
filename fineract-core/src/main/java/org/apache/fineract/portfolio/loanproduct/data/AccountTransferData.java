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
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountTransferData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Boolean reversed;
    private CurrencyData currency;
    private BigDecimal transferAmount;
    private LocalDate transferDate;
    private String transferDescription;
    private OfficeData fromOffice;
    private ClientData fromClient;
    private EnumOptionData fromAccountType;
    private PortfolioAccountData fromAccount;
    private OfficeData toOffice;
    private ClientData toClient;
    private EnumOptionData toAccountType;
    private PortfolioAccountData toAccount;

    // template
    private List<OfficeData> fromOfficeOptions;
    private List<ClientData> fromClientOptions;
    private List<EnumOptionData> fromAccountTypeOptions;
    private List<PortfolioAccountData> fromAccountOptions;
    private List<OfficeData> toOfficeOptions;
    private List<ClientData> toClientOptions;
    private List<EnumOptionData> toAccountTypeOptions;
    private List<PortfolioAccountData> toAccountOptions;
}
