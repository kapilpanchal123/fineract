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
public class GLAccountDataDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Long parentId;
    private String glCode;
    private Boolean disabled;
    private Boolean manualEntriesAllowed;
    private EnumOptionData type;
    private EnumOptionData usage;
    private String description;
    private String nameDecorated;
    private CodeValueDataDTO tagId;
    private Long organizationRunningBalance;

    // templates
    private List<EnumOptionData> accountTypeOptions;
    private List<EnumOptionData> usageOptions;
    private List<GLAccountDataDTO> assetHeaderAccountOptions;
    private List<GLAccountDataDTO> liabilityHeaderAccountOptions;
    private List<GLAccountDataDTO> equityHeaderAccountOptions;
    private List<GLAccountDataDTO> incomeHeaderAccountOptions;
    private List<GLAccountDataDTO> expenseHeaderAccountOptions;
    private List<CodeValueDataDTO> allowedAssetsTagOptions;
    private List<CodeValueDataDTO> allowedLiabilitiesTagOptions;
    private List<CodeValueDataDTO> allowedEquityTagOptions;
    private List<CodeValueDataDTO> allowedIncomeTagOptions;
    private List<CodeValueDataDTO> allowedExpensesTagOptions;

    // import fields
    private transient Integer rowIndex;

}
