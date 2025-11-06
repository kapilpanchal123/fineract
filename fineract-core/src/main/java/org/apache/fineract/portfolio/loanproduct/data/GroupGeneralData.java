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
public class GroupGeneralData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String accountNo;
    private String name;
    private String externalId;
    private EnumOptionData status;
    private Boolean active;
    private LocalDate activationDate;
    private Long officeId;
    private String officeName;
    private Long centerId;
    private String centerName;
    private Long staffId;
    private String staffName;
    private String hierarchy;
    private String groupLevel;

    // associations
    private List<ClientData> clientMembers;
    private List<ClientData> activeClientMembers;
    private List<GroupRoleData> groupRoles;
    private List<CalendarData> calendarsData;
    private CalendarData collectionMeetingCalendar;

    // template
    private List<CenterData> centerOptions;
    private List<OfficeData> officeOptions;
    private List<StaffData> staffOptions;
    private List<ClientData> clientOptions;
    private List<CodeValueData> availableRoles;
    private GroupRoleData selectedRole;
    private List<CodeValueData> closureReasons;
    private GroupTimeLineData timeline;

    private List<DatatableData> datatables;

    // import fields
    private transient Integer rowIndex;
    private String dateFormat;
    private String locale;
    private LocalDate submittedOnDate;
}
