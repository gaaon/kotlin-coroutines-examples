package com.karrot.example

import com.karrot.example.repository.account.UserRxRepository
import com.karrot.example.repository.catalog.ProductReactorRepository
import com.karrot.example.repository.order.OrderFutureRepository
import com.karrot.example.repository.shipment.AddressReactiveRepository
import com.karrot.example.repository.store.StoreMutinyRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.await
import kotlinx.coroutines.jdk9.awaitLast
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx3.awaitSingle

fun main() {
    val userService = UserRxRepository()
    val productService = ProductReactorRepository()
    val orderService = OrderFutureRepository()
    val addressService = AddressReactiveRepository()
    val storeService = StoreMutinyRepository()

    val productIds = listOf("product1", "product2", "product3")
    val userId = "user1"

    runBlocking {
        val buyer = userService.findUserByIdAsMaybe(userId).awaitSingle()
        val address = addressService.findAddressByUserAsPublisher(buyer).awaitLast()
        val products = productService.findAllProductsByIdsAsFlux(productIds).collectList().awaitSingle()
        val stores = storeService.getStoresByProductsAsMulti(products).asFlow().toList()
        val order = orderService.createOrderAsCompletableFuture(buyer, products, stores, address).await()

        println(order)
    }
}
