package com.karrot.example.entity.catalog

import com.karrot.example.vo.Money

class Product(
    val id: String,
    val name: String,
    val price: Money,
    val storeId: String,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Product

        if (id != other.id) return false
        if (name != other.name) return false
        if (price != other.price) return false
        if (storeId != other.storeId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + storeId.hashCode()
        return result
    }

    override fun toString(): String {
        return "Product(id='$id', name='$name', price=$price, storeId='$storeId')"
    }
}
