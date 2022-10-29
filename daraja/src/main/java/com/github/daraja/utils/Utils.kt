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
package com.github.daraja.utils

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
