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
package org.apache.fineract.portfolio.loanproduct.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.accounting.common.AccountingDropdownReadPlatformService;
import org.apache.fineract.accounting.producttoaccountmapping.service.ProductToGLAccountMappingReadPlatformService;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.ApiParameterHelper;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.service.CurrencyReadPlatformService;
import org.apache.fineract.portfolio.charge.service.ChargeReadPlatformService;
import org.apache.fineract.portfolio.common.service.DropdownReadPlatformService;
import org.apache.fineract.portfolio.delinquency.service.DelinquencyReadPlatformService;
import org.apache.fineract.portfolio.floatingrates.service.FloatingRatesReadPlatformService;
import org.apache.fineract.portfolio.fund.service.FundReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanproduct.LoanProductConstants;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.productmix.data.ProductMixData;
import org.apache.fineract.portfolio.loanproduct.productmix.service.ProductMixReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.service.LoanDropdownReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.apache.fineract.portfolio.rate.service.RateReadService;
import org.springframework.stereotype.Component;

@Path("/v2/loanproducts")
@Component
@Tag(name = "Loan Products 2", description = "A Loan product 2 is a template that is used when creating a loan. Much of the template definition can be overridden during loan creation.")
@RequiredArgsConstructor
public class LoanProductsV2ApiResource {

    private static final Set<String> LOAN_PRODUCT_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "name", "shortName", "description",
            "fundId", "fundName", "includeInBorrowerCycle", "currency", "principal", "minPrincipal", "maxPrincipal", "numberOfRepayments",
            "minNumberOfRepayments", "maxNumberOfRepayments", "repaymentEvery", "repaymentFrequencyType", "graceOnPrincipalPayment",
            "recurringMoratoriumOnPrincipalPeriods", "graceOnInterestPayment", "graceOnInterestCharged", "interestRatePerPeriod",
            "minInterestRatePerPeriod", "maxInterestRatePerPeriod", "interestRateFrequencyType", "annualInterestRate", "amortizationType",
            "interestType", "interestCalculationPeriodType", LoanProductConstants.ALLOW_PARTIAL_PERIOD_INTEREST_CALCUALTION_PARAM_NAME,
            "inArrearsTolerance", "transactionProcessingStrategyCode", "transactionProcessingStrategyName", "charges", "accountingRule",
            "externalId", "accountingMappings", "paymentChannelToFundSourceMappings", "fundOptions", "paymentTypeOptions",
            "currencyOptions", "repaymentFrequencyTypeOptions", "interestRateFrequencyTypeOptions", "amortizationTypeOptions",
            "interestTypeOptions", "interestCalculationPeriodTypeOptions", "transactionProcessingStrategyOptions", "chargeOptions",
            "accountingOptions", "accountingRuleOptions", "accountingMappingOptions", "floatingRateOptions",
            "isLinkedToFloatingInterestRates", "floatingRatesId", "interestRateDifferential", "minDifferentialLendingRate",
            "defaultDifferentialLendingRate", "maxDifferentialLendingRate", "isFloatingInterestRateCalculationAllowed",
            LoanProductConstants.CAN_USE_FOR_TOPUP, LoanProductConstants.IS_EQUAL_AMORTIZATION_PARAM, LoanProductConstants.RATES_PARAM_NAME,
            LoanApiConstants.fixedPrincipalPercentagePerInstallmentParamName, LoanProductConstants.DUE_DAYS_FOR_REPAYMENT_EVENT,
            LoanProductConstants.OVER_DUE_DAYS_FOR_REPAYMENT_EVENT, LoanProductConstants.ENABLE_DOWN_PAYMENT,
            LoanProductConstants.DISBURSED_AMOUNT_PERCENTAGE_DOWN_PAYMENT, LoanProductConstants.ENABLE_AUTO_REPAYMENT_DOWN_PAYMENT,
            LoanProductConstants.REPAYMENT_START_DATE_TYPE, LoanProductConstants.DAYS_IN_YEAR_CUSTOM_STRATEGY_TYPE_PARAMETER_NAME,
            LoanProductConstants.ENABLE_INCOME_CAPITALIZATION_PARAM_NAME,
            LoanProductConstants.CAPITALIZED_INCOME_CALCULATION_TYPE_PARAM_NAME,
            LoanProductConstants.CAPITALIZED_INCOME_STRATEGY_PARAM_NAME, LoanProductConstants.CAPITALIZED_INCOME_TYPE_PARAM_NAME,
            LoanProductConstants.ENABLE_BUY_DOWN_FEE_PARAM_NAME, LoanProductConstants.BUY_DOWN_FEE_CALCULATION_TYPE_PARAM_NAME,
            LoanProductConstants.BUY_DOWN_FEE_STRATEGY_PARAM_NAME, LoanProductConstants.BUY_DOWN_FEE_INCOME_TYPE_PARAM_NAME));

    private static final Set<String> PRODUCT_MIX_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList("productId", "productName", "restrictedProducts", "allowedProducts", "productOptions"));

    private static final String RESOURCE_NAME_FOR_PERMISSIONS = "LOANPRODUCT";
    public static final String PRODUCTMIX = "PRODUCTMIX";
    public static final String PRODUCT_MIXES = "productMixes";

    private final PlatformSecurityContext context;
    private final LoanProductReadPlatformService loanProductReadPlatformService;
    private final ChargeReadPlatformService chargeReadPlatformService;
    private final CurrencyReadPlatformService currencyReadPlatformService;
    private final FundReadPlatformService fundReadPlatformService;
    private final DefaultToApiJsonSerializer<LoanProductData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final LoanDropdownReadPlatformService dropdownReadPlatformService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ProductToGLAccountMappingReadPlatformService accountMappingReadPlatformService;
    private final AccountingDropdownReadPlatformService accountingDropdownReadPlatformService;
    private final DefaultToApiJsonSerializer<ProductMixData> productMixDataApiJsonSerializer;
    private final ProductMixReadPlatformService productMixReadPlatformService;
    private final DropdownReadPlatformService commonDropdownReadPlatformService;
    private final PaymentTypeReadPlatformService paymentTypeReadPlatformService;
    private final FloatingRatesReadPlatformService floatingRateReadPlatformService;
    private final RateReadService rateReadService;
    private final ConfigurationDomainService configurationDomainService;
    private final DelinquencyReadPlatformService delinquencyReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "List Loan Products", description = "Lists Loan Products\n\n" + "Example Requests:\n" + "\n" + "loanproducts\n"
            + "\n" + "\n" + "loanproducts?fields=name,description,interestRateFrequencyType,amortizationType")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = LoanProductsApiResourceSwagger.GetLoanProductsResponse.class)))) })
    public String retrieveAllLoanProducts(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS);
        final Set<String> associationParameters = ApiParameterHelper.extractAssociationsForResponseIfProvided(uriInfo.getQueryParameters());
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        if (!associationParameters.isEmpty() && associationParameters.contains(PRODUCT_MIXES)) {
            this.context.authenticatedUser().validateHasReadPermission(PRODUCTMIX);
            final Collection<ProductMixData> productMixes = this.productMixReadPlatformService.retrieveAllProductMixes();
            return this.productMixDataApiJsonSerializer.serialize(settings, productMixes, PRODUCT_MIX_DATA_PARAMETERS);
        }

        final Collection<LoanProductData> products = this.loanProductReadPlatformService.retrieveAllLoanProducts();

        return this.toApiJsonSerializer.serialize(settings, products, LOAN_PRODUCT_DATA_PARAMETERS);
    }
}
