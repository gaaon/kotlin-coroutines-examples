package com.karrot.example

import com.karrot.example.service.account.UserRxService
import com.karrot.example.service.catalog.ProductReactorService
import com.karrot.example.service.order.OrderFutureService
import com.karrot.example.service.shipment.AddressReactiveService
import com.karrot.example.service.store.StoreMutinyService
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.await
import kotlinx.coroutines.jdk9.awaitLast
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx3.awaitSingle

fun main() {
    val userService = UserRxService()
    val productService = ProductReactorService()
    val orderService = OrderFutureService()
    val addressService = AddressReactiveService()
    val storeService = StoreMutinyService()

    val productIds = listOf("product1", "product2", "product3")
    val userId = "user1"

    runBlocking {
        // 0
        val buyer = userService.getUserByIdAsSingle(userId).awaitSingle()

        // 1
        val address = addressService.getAddressByUserAsPublisher(buyer).awaitLast()

        // 2
        val products = productService.getProductsByIdsAsFlux(productIds).collectList().awaitSingle()

        // 3
        val stores = storeService.getStoresByProductsAsMulti(products).asFlow().toList()

        // 4
        val order = orderService.createOrderAsCompletableFuture(buyer, products, stores, address).await()

        // 5
        println(order)
    }
}
