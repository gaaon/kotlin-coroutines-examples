package com.karrot.example.repository.order

import com.karrot.example.entity.account.User
import com.karrot.example.entity.catalog.Product
import com.karrot.example.entity.order.Order
import com.karrot.example.entity.order.OrderItem
import com.karrot.example.entity.store.Store
import com.karrot.example.vo.Address

class OrderSyncRepository {
    fun createOrderSync(
        buyer: User,
        products: List<Product>,
        stores: List<Store>,
        address: Address,
    ): Order {
        val orderItems = products.zip(stores).map { (product, store) ->
            OrderItem(product, store)
        }

        val createdOrder = Order(
            buyer = buyer,
            items = orderItems,
            address = address,
        )

        Thread.sleep(100)
        return createdOrder
    }
}
