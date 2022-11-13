<h1 align="center">android_mpesa_stk</h1>

<p align="center">Android Mpesa library to initiate stk push</p>

<p align="center">
    <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=21+"><img alt="API" src="https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://github.com/skydoves/AndroidVeil/actions"><img alt="Build Status" src="https://github.com/skydoves/TransformationLayout/workflows/Android%20CI/badge.svg"/></a> 
</p><br>

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
Copy this fuction into your `Activity`/`Fragment`/`Composable` and replace with your credentials.

``` Kotlin
private fun sendStkPush(amount: String, phoneNumber: String) {
    val stkPushRequest = STKPushRequest(
        businessShortCode = BUSINESS_SHORT_CODE,
        password = getPassword(BUSINESS_SHORT_CODE, PASS_KEY, timestamp),
        timestamp = timestamp,
        transactionType = "CustomerPayBillOnline",
        amount = amount,
        partyA = sanitizePhoneNumber(phoneNumber),
        partyB = BUSINESS_SHORT_CODE,
        phoneNumber = sanitizePhoneNumber(phoneNumber),
        callBackURL = CALLBACKURL,
        accountReference = "Dlight", // Account reference
        transactionDesc = "Dlight STK PUSH" // Transaction description
    )

    val darajaDriver = DarajaDriver(
        consumerKey = BuildConfig.CONSUMER_KEY,
        consumerSecret = BuildConfig.CONSUMER_SECRET
    )

    lifecycleScope.launch {
        darajaDriver.performStkPush(stkPushRequest).collectLatest { result ->
            when (result) {
                is Resource.Error -> {
                    Toast.makeText(
                        applicationContext,
                        "${result.errorMessage ?: result.error?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Loading -> {
                    Toast.makeText(applicationContext, "Loading...", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    Toast.makeText(
                        applicationContext,
                        "${result.data?.otpResult?.customerMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
```


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
