<h1 align="center">android_mpesa_stk</h1>

<p align="center">
Android Mpesa library to initiate stk push
</p>

<p align="center">
    <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=21+"><img alt="API" src="https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://github.com/skydoves/AndroidVeil/actions"><img alt="Build Status" src="https://github.com/skydoves/TransformationLayout/workflows/Android%20CI/badge.svg"/></a> 
</p><br>

<p>
MPESA just unveiled their new API, Daraja 2.0. It has been hailed as the height of developer heaven with its crisp clear structure, solid security and great syntax. So this library is a wrapper around the API to help you intiate MPESA STK push easily on your Android apps.
</p>

## Download
[![](https://jitpack.io/v/Carrieukie/android_mpesa_stk.svg)](https://jitpack.io/#Carrieukie/android_mpesa_stk)

### Gradle
Add the codes below to your root `build.gradle` file:
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

And add the dependency below to your **module**'s `build.gradle` file:

```gradle
dependencies {
    implementation 'com.github.Carrieukie:android_mpesa_stk:v1.0.2-beta'
}
```

## Usage
Create and Instance of `DarajaDriver` class passing in your credentials and specify your enviroment.

``` Kotlin
private val darajaDriver = DarajaDriver(
    consumerKey = BuildConfig.CONSUMER_KEY,
    consumerSecret = BuildConfig.CONSUMER_SECRET,
    environment = Environment.SandBox()
)
```
> Get the test credentials from Here : https://developer.safaricom.co.ke/test_credentials

Create an instance of `STKPushRequest`

``` Kotlin
private val stkPushRequest = STKPushRequest(
        businessShortCode = BUSINESS_SHORT_CODE,
        password = getPassword(BUSINESS_SHORT_CODE, PASS_KEY, timestamp),
        timestamp = timestamp,
        mpesaTransactionType = TransactionType.CustomerPayBillOnline(),
        amount = amount,
        partyA = sanitizePhoneNumber(phoneNumber),
        partyB = BUSINESS_SHORT_CODE,
        phoneNumber = sanitizePhoneNumber(phoneNumber),
        callBackURL = CALLBACKURL,
        accountReference = "Dlight", // Account reference
        transactionDesc = "Dlight STK PUSH" // Transaction description
    )
 ```


* `businessShortCode` - This is organizations shortcode (Paybill or Buygoods - A 5 to 7 digit account number) used to identify an organization and receive the transaction.
* `password` - This is the password used for encrypting the request sent: A base64 encoded string. (The base64 string is a combination of Shortcode+Passkey+Timestamp)
* `timestamp` - pass in `timestamp` from `com.github.daraja.utils.timestamp` which will generate the timestamp for you. This is the Timestamp of the transaction, normally in the formart of `YEAR+MONTH+DATE+HOUR+MINUTE+SECOND (YYYYMMDDHHMMSS)` Each part should be atleast two digits apart from the year which takes four digits. Use the method `com.github.daraja.utils.getPassword`
* `transactionType` - This is the transaction type that is used to identify the transaction when sending the request to M-Pesa. The transaction type for M-Pesa Express is "CustomerPayBillOnline"
* `amount` - This is the Amount transacted normaly a numeric value. Money that customer pays to the Shorcode. Only whole numbers are supported.
* `partyA` - The phone number sending money. The parameter expected is a Valid Safaricom Mobile Number that is M-Pesa registered in the format 2547XXXXXXXX
* `partyB` - The organization receiving the funds. The parameter expected is a 5 to 7 digit as defined on the Shortcode description above. This can be the same as BusinessShortCode value above.
* `phoneNumber` - The Mobile Number to receive the STK Pin Prompt. This number can be the same as PartyA value above.
* `callbackURL` - A CallBack URL is a valid secure URL that is used to receive notifications from M-Pesa API. It is the endpoint to which the results will be sent by M-Pesa API.
* `accountReference` - Any combinations of letters and numbers
*  `transactionDesc` - This is any additional information/comment that can be sent along with the request from your system. Maximum of 13 Characters.

> Simulate from here : https://developer.safaricom.co.ke/APIs/MpesaExpressSimulate and get your values from this simulation


Use `darajaDriver` object to make `stkPushRequest`


``` Kotlin
    darajaDriver.performStkPush(stkPushRequest)
```


To observe backgroud processes happening when sending the api call, access `darajaState` from the instance of `darajaDriver`


```Kotlin
val darajaStates: StateFlow<DarajaState> = darajaDriver.darajaState
```

which is a state holder for the class

```Kotlin
data class DarajaState(
    val message: String = String(),
    val isLoading: Boolean = false
)
```

Observe changes on the loading state and any message status of the api call by collecting the above stateflow.

## Note

This does not show the transaction status after the api call is sent. See this blog by Ronnie Otieno on how to do that using firebase messaging

> See : https://otieno.medium.com/android-mpesa-integration-using-daraja-library-part-2-5e5d07813963

# License
```xml
Copyright 2022 Carrieukie

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```
