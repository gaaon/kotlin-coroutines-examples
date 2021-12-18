package com.karrot.example.usecase

import com.karrot.example.entity.order.Order
import com.karrot.example.repository.account.UserRxRepository
import com.karrot.example.repository.catalog.ProductReactorRepository
import com.karrot.example.repository.order.OrderFutureRepository
import com.karrot.example.repository.shipment.AddressReactiveRepository
import com.karrot.example.repository.store.StoreMutinyRepository
import reactor.adapter.JdkFlowAdapter
import reactor.adapter.rxjava.RxJava3Adapter
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class CreateOrderReactorUseCase(
    private val userService: UserRxRepository,
    private val productService: ProductReactorRepository,
    private val orderService: OrderFutureRepository,
    private val addressService: AddressReactiveRepository,
    private val storeService: StoreMutinyRepository,
) : CreateOrderUseCaseBase() {
    data class InputValues(
        val userId: String,
        val productIds: List<String>,
    )

    fun execute(inputValues: InputValues): Mono<Order> {
        val (userId, productIds) = inputValues

        val createdOrder = RxJava3Adapter.maybeToMono(userService.findUserByIdAsMaybe(userId))
            .flatMap { buyer ->
                JdkFlowAdapter.flowPublisherToFlux(addressService.findAddressByUserAsPublisher(buyer))
                    .last()
                    .flatMap { address ->
                        isValidRegion(address)
                        productService.findAllProductsByIdsAsFlux(productIds)
                            .collectList()
                            .flatMap { products ->
                                check(products.isNotEmpty())
                                Flux.from(storeService.getStoresByProductsAsMulti(products))
                                    .collectList()
                                    .flatMap { stores ->
                                        check(stores.isNotEmpty())
                                        Mono.fromFuture(
                                            orderService.createOrderAsCompletableFuture(
                                                buyer, products, stores, address
                                            ).toCompletableFuture()
                                        )
                                    }
                            }
                    }
            }

        return createdOrder
    }
}
