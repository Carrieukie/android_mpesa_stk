package com.example.daraja

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.github.daraja.drivertwopointo.DarajaDriverTwoPointO
import com.github.daraja.model.requests.STKPushRequest
import com.github.daraja.utils.getPassword
import com.github.daraja.utils.sanitizePhoneNumber
import com.github.daraja.utils.timestamp

class MainViewModel : ViewModel() {

    private val darajaDriver = DarajaDriverTwoPointO(
        consumerKey = BuildConfig.CONSUMER_KEY,
        consumerSecret = BuildConfig.CONSUMER_SECRET
    )

    val phoneState = mutableStateOf(TextFieldValue("0710102720"))
    val amount = mutableStateOf(TextFieldValue("1"))
    val dialogState = mutableStateOf(false)
    val darajaStates = darajaDriver.darajaState

    fun sendStkPush(amount: String, phoneNumber: String) {
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
