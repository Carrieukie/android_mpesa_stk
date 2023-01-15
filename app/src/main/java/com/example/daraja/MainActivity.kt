/*
 * Copyright 2022 Android M-pesa STK
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
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.daraja.ui.theme.DarajaTheme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DarajaTheme {
                Column(
                    Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(text = "Enter phone number") },
                        value = viewModel.phoneState.value,
                        onValueChange = { viewModel.phoneState.value = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(text = "Enter amount") },
                        value = viewModel.amount.value,
                        onValueChange = { viewModel.amount.value = it }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(onClick = {
                        viewModel.sendStkPush(
                            viewModel.amount.value.text,
                            viewModel.phoneState.value.text
                        )
                        viewModel.dialogState.value = true
                    }) {
                        Text(text = "Initiate Payment")
                    }

                    if (viewModel.dialogState.value) {
                        DialogBoxLoading()
                    }
                }
            }
        }
    }

    @Composable
    fun DialogBoxLoading() {
        Dialog(
            onDismissRequest = { viewModel.dialogState.value = false }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(White, shape = RoundedCornerShape(12.dp))
                    .fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                ) {
                    if (viewModel.darajaStates.collectAsState().value.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                    Text(
                        text = viewModel.darajaStates.collectAsState().value.message,
                        Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
