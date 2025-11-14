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
import java.util.Set;
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
public class ClientDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String accountNumber;
    private OfficeDTO office;
    private OfficeDTO transferToOffice;
    private ImageDTO image;
    private Integer status;
    private CodeValueDTO subStatus;
    private LocalDate activationDate;
    private LocalDate officeJoiningDate;
    private String firstname;
    private String middlename;
    private String lastname;
    private String fullname;
    private String displayName;
    private String mobileNo;
    private String emailAddress;
    private Boolean isStaff;
    private ExternalIdDTO externalId;
    private LocalDate dateOfBirth;
    private CodeValueDTO gender;
    private StaffDTO staff;
    private Set<GroupDTO> groups;
    private CodeValueDTO closureReason;
    private LocalDate closureDate;
    private CodeValueDTO rejectionReason;
    private LocalDate rejectionDate;
    private AppUserDTO rejectedBy;
    private CodeValueDTO withdrawalReason;
    private LocalDate withdrawalDate;
    private AppUserDTO withdrawnBy;
    private LocalDate reactivateDate;
    private AppUserDTO reactivatedBy;
    private AppUserDTO closedBy;
    private LocalDate submittedOnDate;
    private AppUserDTO activatedBy;
    private Long savingsProductId;
    private Long savingsAccountId;
    private CodeValueDTO clientType;
    private CodeValueDTO clientClassification;
    private Integer legalForm;
    private LocalDate reopenedDate;
    private AppUserDTO reopenedBy;
    private LocalDate proposedTransferDate;
    protected Set<ClientIdentifierDTO> identifiers;
}
