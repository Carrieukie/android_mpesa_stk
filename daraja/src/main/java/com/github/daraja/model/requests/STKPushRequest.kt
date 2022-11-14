/*
 * Copyright 2022 Eric Kariuki Kimani.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.daraja.model.requests

import com.google.gson.annotations.SerializedName

/**
 * A class representation of the request sent to initiate an STK push
 * @param businessShortCode : This is organizations shortcode (Paybill or Buygoods - A 5 to 7 digit
 * account number) used to identify an organization and receive the transaction. eg Shortcode
 * (5 to 7 digits) e.g. 654321
 *
 * @param password : This is the password used for encrypting the request sent: A base64 encoded
 * string. (The base64 string is a combination of Shortcode+Passkey+Timestamp)
 * i.e. base64.encode(Shortcode+Passkey+Timestamp)
 *
 * @param timestamp : This is the Timestamp of the transaction, normaly in the formart of
 * YEAR+MONTH+DATE+HOUR+MINUTE+SECOND (YYYYMMDDHHMMSS) Each part should be atleast two digits apart
 * from the year which takes four digits. {@link com.github.daraja.utils.timestamp}
 *
 *
 * @param transactionType : This is the transaction type that is used to identify the transaction
 * when sending the request to M-Pesa. The transaction type for M-Pesa Express
 * is "CustomerPayBillOnline". Can be either CustomerPayBillOnline, CustomerBuyGoodsOnline
 *
 * @param amount : This is the Amount transacted normaly a numeric value. Money that customer pays
 * to the Shortcode. Only whole numbers are supported. e.g 1000
 *
 * @param partyA : The phone number sending money. The parameter expected is a Valid Safaricom
 * Mobile Number that is M-Pesa registered in the format 2547XXXXXXXX e.g 254712345678
 *
 * @param partyB : The organization receiving the funds. The parameter expected is a 5 to 7 digit
 * as defined on the Shortcode description above. This can be the same as BusinessShortCode
 * value above. e.g. 174379
 *
 * @param phoneNumber : The Mobile Number to receive the STK Pin Prompt. This number can be the
 * same as PartyA value above. e.g 254712345678
 *
 * @param callBackURL : A CallBack URL is a valid secure URL that is used to receive notifications from M-Pesa
 * API. It is the endpoint to which the results will be sent by M-Pesa API. e.g https://ip or domain:port/path, e.g: https://mydomain.com/path, https://0.0.0.0:9090/path
 *
 * @param transactionDesc : This is an Alpha-Numeric parameter that is defined by your system as
 * an Identifier of the transaction for CustomerPayBillOnline transaction type.
 * Along with the business name, this value is also displayed to the customer in the STK Pin
 * Prompt message. Maximum of 12 characters.
 *
 */
data class STKPushRequest(

    @field:SerializedName("BusinessShortCode")
    val businessShortCode: String,

    @field:SerializedName("Password")
    val password: String,

    @field:SerializedName("Timestamp")
    val timestamp: String,

    @field:SerializedName("TransactionType")
    val transactionType: String,

    @field:SerializedName("Amount")
    val amount: String,

    @field:SerializedName("PartyA")
    val partyA: String,

    @field:SerializedName("PartyB")
    val partyB: String,

    @field:SerializedName("PhoneNumber")
    val phoneNumber: String,

    @field:SerializedName("CallBackURL")
    val callBackURL: String,

    @field:SerializedName("AccountReference")
    val accountReference: String,

    @field:SerializedName("TransactionDesc")
    val transactionDesc: String

)
