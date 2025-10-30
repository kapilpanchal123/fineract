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
package org.apache.fineract.infrastructure.core.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.exception.PlatformInternalServerException;

@Getter
@EqualsAndHashCode
public class ExternalId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;

    private static final ExternalId empty = new ExternalId();
    private final String value;

    private ExternalId() {
        this.value = null;
    }

    public ExternalId(String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("error.external.id.cannot.be.blank");
        }
        this.value = value;
    }

    public static ExternalId generate() {
        return new ExternalId(UUID.randomUUID().toString());
    }

    public static ExternalId empty() {
        return empty;
    }

    public boolean isEmpty() {
        return value == null;
    }

    public void throwExceptionIfEmpty() {
        if (isEmpty()) {
            throw new PlatformInternalServerException("error.external.id.is.not.set", "Internal state violation: External id is not set");
        }
    }
}
