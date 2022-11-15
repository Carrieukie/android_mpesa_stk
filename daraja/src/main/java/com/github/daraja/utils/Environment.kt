package com.github.daraja.utils

sealed class Environment(
    val url: String
) {
    class SandBox() : Environment("https://sandbox.safaricom.co.ke/")
    class PRODUCTION() : Environment("https://api.safaricom.co.ke/")
}
