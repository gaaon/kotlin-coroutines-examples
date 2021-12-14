package com.karrot.example.entity.order

import com.karrot.example.entity.account.User
import com.karrot.example.vo.Address

class Order(
    val buyer: User,
    val items: List<OrderItem>,
    val address: Address,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Order

        if (buyer != other.buyer) return false
        if (items != other.items) return false
        if (address != other.address) return false

        return true
    }

    override fun hashCode(): Int {
        var result = buyer.hashCode()
        result = 31 * result + items.hashCode()
        result = 31 * result + address.hashCode()
        return result
    }

    override fun toString(): String {
        return "Order(buyer=$buyer, items=$items, address=$address)"
    }
}
