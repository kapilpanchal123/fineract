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
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.savings.data.SavingsProductData;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String accountNo;
    private ExternalIdData externalId;

    private EnumOptionData status;
    private CodeValueData subStatus;

    private Boolean active;
    private LocalDate activationDate;

    private String firstname;
    private String middlename;
    private String lastname;
    private String fullname;
    private String displayName;
    private String mobileNo;
    private String emailAddress;
    private LocalDate dateOfBirth;
    private CodeValueData gender;
    private CodeValueData clientType;
    private CodeValueData clientClassification;
    private Boolean isStaff;

    private Long officeId;
    private String officeName;
    private Long transferToOfficeId;
    private String transferToOfficeName;

    private Long imageId;
    private Boolean imagePresent;
    private Long staffId;
    private String staffName;
    private ClientTimeLineData timeline;

    private Long savingsProductId;
    private String savingsProductName;

    private Long savingsAccountId;
    private EnumOptionData legalForm;
    private Set<ClientCollateralManagementData> clientCollateralManagements;

    // associations
    private List<GroupGeneralData> groups;

    // template
    private List<OfficeData> officeOptions;
    private List<StaffData> staffOptions;
    private List<org.apache.fineract.infrastructure.codes.data.CodeValueData> narrations;
    private List<SavingsProductData> savingProductOptions;
    private List<SavingsAccountData> savingAccountOptions;
    private List<CodeValueData> genderOptions;
    private List<CodeValueData> clientTypeOptions;
    private List<CodeValueData> clientClassificationOptions;
    private List<CodeValueData> clientNonPersonConstitutionOptions;
    private List<CodeValueData> clientNonPersonMainBusinessLineOptions;
    private List<EnumOptionData> clientLegalFormOptions;
    private ClientFamilyMembersData familyMemberOptions;

    private ClientNonPersonData clientNonPersonDetails;

    private List<AddressData> address;

    private Boolean isAddressEnabled;

    private List<DatatableData> datatables;

    // import fields
    private transient Integer rowIndex;
    private String dateFormat;
    private String locale;
    private Long clientTypeId;
    private Long genderId;
    private Long clientClassificationId;
    private Long legalFormId;
    private LocalDate submittedOnDate;
}
