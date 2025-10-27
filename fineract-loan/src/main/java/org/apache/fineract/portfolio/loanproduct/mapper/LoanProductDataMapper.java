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

import org.apache.fineract.infrastructure.core.config.MapstructMapperConfig;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.rate.data.RateData;
import org.apache.fineract.portfolio.rate.domain.Rate;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(config = MapstructMapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanProductDataMapper {
  LoanProductData fromLoanAccount(LoanProduct loanAccount);

  default RateData map (Rate rate) {
    if(rate == null) {
      return null;
    }
    return RateData.builder()
        .name(rate.getName())
        .percentage(rate.getPercentage())
        .productApply(new EnumOptionData(rate.getId(), rate.getName(),""))
        .active(rate.isActive())
        .build();
  }

  default List<RateData> map(List<Rate> rates) {
    if(rates == null) {
      return null;
    }

    return rates.stream().map(this::map).collect(Collectors.toList());
  }

  default EnumOptionData map() {

  }
}
