package com.karrot.example

import com.karrot.example.service.account.UserRxService
import com.karrot.example.service.catalog.ProductReactorService
import com.karrot.example.service.order.OrderFutureService
import com.karrot.example.service.shipment.AddressReactiveService
import com.karrot.example.service.store.StoreMutinyService
import reactor.adapter.JdkFlowAdapter
import reactor.adapter.rxjava.RxJava3Adapter
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

fun main() {
    val userService = UserRxService()
    val productService = ProductReactorService()
    val orderService = OrderFutureService()
    val addressService = AddressReactiveService()
    val storeService = StoreMutinyService()

    val productIds = listOf("product1", "product2", "product3")
    val userId = "user1"

    RxJava3Adapter.maybeToMono(userService.getUserByIdAsMaybe(userId))
        .flatMap { buyer ->
            JdkFlowAdapter.flowPublisherToFlux(addressService.getAddressByUserAsPublisher(buyer))
                .last()
                .flatMap { address ->
                    productService.getProductsByIdsAsFlux(productIds)
                        .collectList()
                        .flatMap { products ->
                            Flux.from(storeService.getStoresByProductsAsMulti(products))
                                .collectList()
                                .flatMap { stores ->
                                    Mono.fromFuture(
                                        orderService.createOrderAsCompletableFuture(
                                            buyer, products, stores, address
                                        ).toCompletableFuture()
                                    )
                                }
                        }
                }
        }.subscribe {
            println(it)
        }

    Thread.sleep(1000)
}
