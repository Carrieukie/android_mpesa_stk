package com.example.daraja

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val phoneState = mutableStateOf(TextFieldValue("0710102720"))
    val amount = mutableStateOf(TextFieldValue("1"))
}
