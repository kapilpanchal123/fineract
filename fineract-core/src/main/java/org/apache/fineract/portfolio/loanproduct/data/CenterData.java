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
import java.util.Collection;
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
public class CenterData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String accountNo;
    private String name;
    private String externalId;
    private Long officeId;
    private String officeName;
    private Long staffId;
    private String staffName;
    private String hierarchy;

    private EnumOptionData status;
    private boolean active;
    private LocalDate activationDate;

    private GroupTimeLineData timeline;
    // associations
    private Collection<GroupGeneralData> groupMembers;

    // template
    private Collection<GroupGeneralData> groupMembersOptions;
    private CalendarData collectionMeetingCalendar;
    private Collection<CodeValueData> closureReasons;
    private Collection<OfficeData> officeOptions;
    private Collection<StaffData> staffOptions;
    private BigDecimal totalCollected;
    private BigDecimal totalOverdue;
    private BigDecimal totaldue;
    private BigDecimal installmentDue;

    private List<DatatableData> datatables;

    // import fields
    private transient Integer rowIndex;
    private String dateFormat;
    private String locale;
    private LocalDate submittedOnDate;
}
