package com.karrot.example.entity.order

import com.karrot.example.entity.catalog.Product
import com.karrot.example.entity.store.Store

class OrderItem(
    val product: Product,
    val store: Store,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrderItem

        if (product != other.product) return false

        return true
    }

    override fun hashCode(): Int {
        return product.hashCode()
    }

    override fun toString(): String {
        return "OrderItem(product=$product)"
    }
}
