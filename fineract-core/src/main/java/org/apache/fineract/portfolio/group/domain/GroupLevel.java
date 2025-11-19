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
package org.apache.fineract.portfolio.group.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "m_group_level")
public class GroupLevel extends AbstractPersistableCustom<Long> {

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "super_parent", nullable = false)
    private Boolean superParent;

    @Column(name = "level_name", nullable = false, length = 100, unique = true)
    private String levelName;

    @Column(name = "recursable", nullable = false)
    private Boolean recursable = false;

    @Column(name = "can_have_clients", nullable = false)
    private Boolean canHaveClients = false;

    public Boolean isIdentifiedByParentId(final Long parentLevelId) {
        return this.parentId.equals(parentLevelId);
    }

    public Boolean isCenter() {
        return this.levelName.equalsIgnoreCase("Center");
    }

    public Boolean isGroup() {
        return this.levelName.equalsIgnoreCase("Group");
    }
}
