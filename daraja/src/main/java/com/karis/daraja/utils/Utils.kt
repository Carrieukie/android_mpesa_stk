package com.karis.daraja.utils

import android.util.Base64
import java.text.SimpleDateFormat
import java.util.*

val timestamp: String
    get() = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())

fun sanitizePhoneNumber(phone: String): String {
    if (phone == "") {
        return ""
    }
    if (phone.length < 11 && phone.startsWith("0")) {
        return phone.replaceFirst("^0".toRegex(), "254")
    }
    return if (phone.length == 13 && phone.startsWith("+")) {
        phone.replaceFirst("^+".toRegex(), "")
    } else phone
}

fun getPassword(businessShortCode: String, passkey: String, timestamp: String): String {
    val str = businessShortCode + passkey + timestamp
    // encode the password to Base64
    return Base64.encodeToString(str.toByteArray(), Base64.NO_WRAP)
}
