package com.netlab.suhan.data

data class Order(
    var orderId: Int,
    var custId: Int,
    var menu: String,
    var qty: Int,
    var price: Double
)
