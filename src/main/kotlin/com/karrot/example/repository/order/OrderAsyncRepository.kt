package com.karrot.example.repository.order

import com.karrot.example.entity.account.User
import com.karrot.example.entity.catalog.Product
import com.karrot.example.entity.order.Order
import com.karrot.example.entity.store.Store
import com.karrot.example.vo.Address
import java.util.concurrent.CompletableFuture

interface OrderAsyncRepository {
    fun createOrderAsCompletableFuture(
        buyer: User,
        products: List<Product>,
        stores: List<Store>,
        address: Address,
    ): CompletableFuture<Order>
}
