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
package org.apache.fineract.portfolio.loanproduct.mapper;

import java.util.List;
import java.util.Map;
import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.infrastructure.core.config.MapstructMapperConfig;
import org.apache.fineract.portfolio.loanproduct.data.GLAccountDataDTO;
import org.apache.fineract.portfolio.loanproduct.data.TaxComponentDataDTO;
import org.apache.fineract.portfolio.tax.data.TaxComponentData;
import org.mapstruct.Mapper;

@Mapper(config = MapstructMapperConfig.class, uses = { GLAccountDataToGLAccountDataDTOMapper.class,
        TaxComponentHistoryDataToTaxComponentHistoryDataDTOMapper.class })
public interface TaxComponentDataToTaxComponentDataDTOMapper {

    TaxComponentDataDTO fromTaxComponentData(TaxComponentData taxComponentData);

    TaxComponentData toTaxComponentData(TaxComponentDataDTO taxComponentDataDTO);

    Map<String, List<GLAccountDataDTO>> map(Map<String, List<GLAccountData>> value);

    Map<String, List<GLAccountData>> mapDto(Map<String, List<GLAccountDataDTO>> value);

    List<GLAccountDataDTO> map(List<GLAccountData> value);

    List<GLAccountData> mapDto(List<GLAccountDataDTO> value);
}
