package com.karrot.example

import com.karrot.example.entity.account.User
import com.karrot.example.entity.catalog.Product
import com.karrot.example.entity.order.Order
import com.karrot.example.entity.store.Store
import com.karrot.example.repository.account.UserRxRepository
import com.karrot.example.repository.catalog.ProductReactorRepository
import com.karrot.example.repository.order.OrderFutureRepository
import com.karrot.example.repository.shipment.AddressReactiveRepository
import com.karrot.example.repository.shipment.LastItemSubscriber
import com.karrot.example.repository.store.StoreMutinyRepository
import com.karrot.example.vo.Address


fun main() {
    val userService = UserRxRepository()
    val productService = ProductReactorRepository()
    val orderService = OrderFutureRepository()
    val addressService = AddressReactiveRepository()
    val storeService = StoreMutinyRepository()

    val productIds = listOf("product1", "product2", "product3")
    val userId = "user1"

    var label = 0

    lateinit var buyer: User
    lateinit var products:  List<Product>
    lateinit var stores: List<Store>
    lateinit var address: Address

    fun resume(value: Any) {
        when (label) {
            0 -> {
                userService.findUserByIdAsMaybe(userId)
                    .subscribe { user ->
                        label++
                        resume(user)
                    }
            }
            1 -> {
                buyer = value as User
                addressService.findAddressByUserAsPublisher(buyer)
                    .subscribe(LastItemSubscriber { address ->
                        label++
                        resume(address)
                    })
            }
            2 -> {
                address = value as Address
                productService.findAllProductsByIdsAsFlux(productIds).collectList()
                    .subscribe { products ->
                        label++
                        resume(products)
                    }
            }
            3 -> {
                products = value as List<Product>
                storeService.getStoresByProductsAsMulti(products)
                    .collect()
                    .asList()
                    .subscribeAsCompletionStage()
                    .whenComplete { stores, _ ->
                        label++
                        resume(stores)
                    }
            }
            4 -> {
                stores = value as List<Store>
                orderService.createOrderAsCompletableFuture(buyer, products, stores, address)
                    .whenComplete { order, _ ->
                        label++
                        resume(order)
                    }
            }
            5 -> {
                val order = value as Order
                label++
                println(order)
            }
            else -> throw IllegalAccessException()
        }
    }

    resume(Unit)

    while (true) {
        Thread.sleep(100)
        if (label == 6) break
    }
}
