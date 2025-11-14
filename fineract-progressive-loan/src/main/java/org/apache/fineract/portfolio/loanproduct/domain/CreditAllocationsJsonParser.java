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
package org.apache.fineract.portfolio.loanproduct.domain;

import com.google.common.base.Enums;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.loanproduct.data.AllocationTypeENUM;
import org.apache.fineract.portfolio.loanproduct.data.CreditAllocationDataDTO;
import org.apache.fineract.portfolio.loanproduct.data.CreditAllocationOrderDTO;
import org.apache.fineract.portfolio.loanproduct.data.CreditAllocationTransactionTypeENUM;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductCreditAllocationRuleDTO;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductRequestDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreditAllocationsJsonParser {

    public final CreditAllocationsValidator creditAllocationsValidator;

    public List<LoanProductCreditAllocationRule> assembleLoanProductCreditAllocationRules(final JsonCommand command,
            String loanTransactionProcessingStrategyCode) {
        JsonArray creditAllocation = command.arrayOfParameterNamed("creditAllocation");
        List<LoanProductCreditAllocationRule> productCreditAllocationRules = null;
        if (creditAllocation != null) {
            productCreditAllocationRules = creditAllocation.asList().stream().map(json -> {
                Map<String, JsonElement> map = json.getAsJsonObject().asMap();
                LoanProductCreditAllocationRule creditAllocationRule = new LoanProductCreditAllocationRule();
                populateCreditAllocationRules(map, creditAllocationRule);
                populateTransactionType(map, creditAllocationRule);
                return creditAllocationRule;
            }).toList();
        }
        creditAllocationsValidator.validate(productCreditAllocationRules, loanTransactionProcessingStrategyCode);
        return productCreditAllocationRules;
    }

    private void populateCreditAllocationRules(Map<String, JsonElement> map, LoanProductCreditAllocationRule creditAllocationRule) {
        JsonArray creditAllocationOrder = asJsonArrayOrNull(map.get("creditAllocationOrder"));
        if (creditAllocationOrder != null) {
            creditAllocationRule.setAllocationTypes(getAllocationTypes(creditAllocationOrder));
        }
    }

    private void populateTransactionType(Map<String, JsonElement> map, LoanProductCreditAllocationRule creditAllocationRule) {
        String transactionType = asStringOrNull(map.get("transactionType"));
        if (transactionType != null) {
            creditAllocationRule.setTransactionType(Enums.getIfPresent(CreditAllocationTransactionType.class, transactionType).orNull());
        }
    }

    @NonNull
    private List<AllocationType> getAllocationTypes(JsonArray allocationOrder) {
        if (allocationOrder != null) {
            List<Pair<Integer, AllocationType>> parsedListWithOrder = allocationOrder.asList().stream().map(json -> {
                Map<String, JsonElement> map = json.getAsJsonObject().asMap();
                AllocationType allocationType = null;
                String creditAllocationRule = asStringOrNull(map.get("creditAllocationRule"));
                if (creditAllocationRule != null) {
                    allocationType = Enums.getIfPresent(AllocationType.class, creditAllocationRule).orNull();
                }
                return Pair.of(asIntegerOrNull(map.get("order")), allocationType);
            }).sorted(Comparator.comparing(Pair::getLeft)).toList();
            creditAllocationsValidator.validatePairOfOrderAndCreditAllocationType(parsedListWithOrder);
            return parsedListWithOrder.stream().map(Pair::getRight).toList();
        } else {
            return List.of();
        }
    }

    @NonNull
    private List<AllocationTypeENUM> getAllocationTypesDTO(CreditAllocationDataDTO allocationOrder) {
      List<AllocationTypeENUM> results = new ArrayList<>();
//      if (allocationOrder != null) {
//        List<Pair<Integer, AllocationType>> parsedListWithOrder = allocationOrder.asList().stream().map(json -> {
//          Map<String, JsonElement> map = json.getAsJsonObject().asMap();
//          AllocationType allocationType = null;
//          String creditAllocationRule = asStringOrNull(map.get("creditAllocationRule"));
//          if (creditAllocationRule != null) {
//            allocationType = Enums.getIfPresent(AllocationType.class, creditAllocationRule).orNull();
//          }
//          return Pair.of(asIntegerOrNull(map.get("order")), allocationType);
//        } else {
//          return List.of();
//        }  }).sorted(Comparator.comparing(Pair::getLeft)).toList();
//        creditAllocationsValidator.validatePairOfOrderAndCreditAllocationType(parsedListWithOrder);
//        return parsedListWithOrder.stream().map(Pair::getRight).toList();

      if(!allocationOrder.getCreditAllocationOrder().isEmpty()) {
        for(CreditAllocationOrderDTO orderElement: allocationOrder.getCreditAllocationOrder()) {
          String creditAllocationRule = orderElement.getCreditAllocationRule();
          if(!creditAllocationRule.isEmpty()) {
            results.add(Enums.getIfPresent(AllocationTypeENUM.class, creditAllocationRule).orNull());
          }
        }
      }
      return results;
    }

    private Integer asIntegerOrNull(JsonElement element) {
        if (!element.isJsonNull()) {
            return element.getAsInt();
        }
        return null;
    }

    private String asStringOrNull(JsonElement element) {
        if (!element.isJsonNull()) {
            return element.getAsString();
        }
        return null;
    }

    private JsonArray asJsonArrayOrNull(JsonElement element) {
        if (!element.isJsonNull()) {
            return element.getAsJsonArray();
        }
        return null;
    }

    public List<LoanProductCreditAllocationRuleDTO> assembleLoanProductCreditAllocationRules(LoanProductRequestDTO loanProductRequestDTO) {
//      JsonArray creditAllocation = command.arrayOfParameterNamed("creditAllocation");
      List<LoanProductCreditAllocationRuleDTO> productCreditAllocationRules = new ArrayList<>();
//      if (creditAllocation != null) {
//        productCreditAllocationRules = creditAllocation.asList().stream().map(json -> {
//          Map<String, JsonElement> map = json.getAsJsonObject().asMap();
//          LoanProductCreditAllocationRule creditAllocationRule = new LoanProductCreditAllocationRule();
//          populateCreditAllocationRules(map, creditAllocationRule);
//          populateTransactionType(map, creditAllocationRule);
//          return creditAllocationRule;
//        }).toList();
//      }

      if (!loanProductRequestDTO.getCreditAllocation().isEmpty()) {
//        productCreditAllocationRules = creditAllocation.asList().stream().map(json -> {
//          Map<String, JsonElement> map = json.getAsJsonObject().asMap();
//          LoanProductCreditAllocationRule creditAllocationRule = new LoanProductCreditAllocationRule();
//          populateCreditAllocationRules(map, creditAllocationRule);
//          populateTransactionType(map, creditAllocationRule);
//          return creditAllocationRule;
//        }).toList();

        for(CreditAllocationDataDTO innerElement: loanProductRequestDTO.getCreditAllocation()) {
          LoanProductCreditAllocationRuleDTO creditAllocationRule = new LoanProductCreditAllocationRuleDTO();
          creditAllocationRule.setAllocationTypes(getAllocationTypesDTO(innerElement));
          creditAllocationRule.setTransactionType(Enums.getIfPresent(CreditAllocationTransactionTypeENUM.class, innerElement.getTransactionType()).orNull());
          productCreditAllocationRules.add(creditAllocationRule);
        }
      }
      return productCreditAllocationRules;
//      creditAllocationsValidator.validate(productCreditAllocationRules, loanTransactionProcessingStrategyCode);
//      return productCreditAllocationRules;
//      return Collections.emptyList();
    }
}
