/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.nationalinsurancedesstub.controllers

import play.api.http.Status._

// This error response does not match the standard error response that is documented in the Dev Hub reference guide
// Leaving it as it is for now so as to avoid a breaking change.
sealed abstract class ErrorResponse(val httpStatusCode: Int,
                                    val message: String)

case object ErrorSaUtrInvalid extends ErrorResponse(BAD_REQUEST, "ERROR_SA_UTR_INVALID")

case object ErrorTaxYearInvalid extends ErrorResponse(BAD_REQUEST, "ERROR_TAX_YEAR_INVALID")

case object ErrorGenericBadRequest extends ErrorResponse(BAD_REQUEST, "ERROR_BAD_REQUEST")

case object ErrorNotFound extends ErrorResponse(NOT_FOUND, "ERROR_NOT_FOUND")
