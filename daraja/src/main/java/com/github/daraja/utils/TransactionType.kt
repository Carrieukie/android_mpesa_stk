package com.github.daraja.utils

sealed class TransactionType(
    val type: String
) {
    class CustomerPayBillOnline : TransactionType("CustomerPayBillOnline")
    class CustomerBuyGoodsOnline : TransactionType("CustomerBuyGoodsOnline")
}
