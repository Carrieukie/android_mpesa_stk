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
package com.example.daraja

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.daraja.ui.theme.DarajaTheme
import com.github.daraja.drivertwo.DarajaDriverTwoPointO
import com.github.daraja.drivertwo.DarajaState
import com.github.daraja.model.requests.STKPushRequest
import com.github.daraja.utils.getPassword
import com.github.daraja.utils.sanitizePhoneNumber
import com.github.daraja.utils.timestamp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val darajaDriver = DarajaDriverTwoPointO(
        consumerKey = BuildConfig.CONSUMER_KEY,
        consumerSecret = BuildConfig.CONSUMER_SECRET
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DarajaTheme {
                Column(
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(.4F),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    val phoneState = remember { mutableStateOf(TextFieldValue("0706003891")) }
                    TextField(
                        placeholder = { Text(text = "Enter phone number") },
                        value = phoneState.value,
                        onValueChange = { phoneState.value = it }
                    )

                    val amount = remember { mutableStateOf(TextFieldValue("1")) }
                    TextField(
                        placeholder = { Text(text = "Enter amount") },
                        value = amount.value,
                        onValueChange = { amount.value = it }
                    )

                    Button(onClick = {
                        sendStkPush(amount.value.text, phoneState.value.text)
                    }) {
                        Text(text = "Initiate Payment")
                    }
                }
            }
        }
        observeDarajaState()
    }

    private fun observeDarajaState() {
        lifecycleScope.launch {
            darajaDriver.darajaState.collectLatest { darajaState: DarajaState ->
                Toast.makeText(this@MainActivity, darajaState.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendStkPush(amount: String, phoneNumber: String) {
        val stkPushRequest = STKPushRequest(
            businessShortCode = Constants.BUSINESS_SHORT_CODE,
            password = getPassword(Constants.BUSINESS_SHORT_CODE, BuildConfig.PASS_KEY, timestamp),
            timestamp = timestamp,
            transactionType = "CustomerPayBillOnline",
            amount = amount,
            partyA = sanitizePhoneNumber(phoneNumber),
            partyB = Constants.PARTYB,
            phoneNumber = sanitizePhoneNumber(phoneNumber),
            callBackURL = Constants.CALLBACKURL,
            accountReference = "Dlight", // Account reference
            transactionDesc = "Dlight STK PUSH " // Transaction description
        )
        darajaDriver.performStkPush(stkPushRequest)
    }
}
