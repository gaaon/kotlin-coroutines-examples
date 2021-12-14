package com.karrot.example.service.order

import com.karrot.example.entity.account.User
import com.karrot.example.entity.catalog.Product
import com.karrot.example.entity.order.Order
import com.karrot.example.entity.order.OrderItem
import com.karrot.example.entity.store.Store
import com.karrot.example.vo.Address
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.TimeUnit

class OrderFutureService {
    fun createOrderAsCompletableFuture(
        buyer: User,
        products: List<Product>,
        stores: List<Store>,
        address: Address,
    ): CompletionStage<Order> {
        val orderItems = products.zip(stores).map { (product, store) ->
            OrderItem(product, store)
        }

        val createdOrder = Order(
            buyer = buyer,
            items = orderItems,
            address = address,
        )

        val delayed = CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS)
        return CompletableFuture.supplyAsync({ createdOrder }, delayed)
    }
}
