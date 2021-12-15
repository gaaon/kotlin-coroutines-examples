package com.karrot.example

import com.karrot.example.service.account.UserRxService
import com.karrot.example.service.catalog.ProductReactorService
import com.karrot.example.service.order.OrderFutureService
import com.karrot.example.service.shipment.AddressReactiveService
import com.karrot.example.service.shipment.AddressSubscriber
import com.karrot.example.service.store.StoreMutinyService

fun main() {
    val userService = UserRxService()
    val productService = ProductReactorService()
    val orderService = OrderFutureService()
    val addressService = AddressReactiveService()
    val storeService = StoreMutinyService()

    val productIds = listOf("product1", "product2", "product3")
    val userId = "user1"

    userService.getUserByIdAsMaybe(userId)
        .subscribe { buyer ->
            addressService.getAddressByUserAsPublisher(buyer)
                .subscribe(
                    AddressSubscriber { address ->
                        productService.getProductsByIdsAsFlux(productIds)
                            .collectList()
                            .subscribe { products ->
                                storeService.getStoresByProductsAsMulti(products)
                                    .collect().asList()
                                    .subscribeAsCompletionStage()
                                    .whenComplete { stores, _ ->
                                        orderService.createOrderAsCompletableFuture(
                                            buyer, products, stores, address
                                        ).whenComplete { order, _ ->
                                            println(order)
                                        }
                                    }
                            }
                    }
                )
        }

    Thread.sleep(1000)
}
