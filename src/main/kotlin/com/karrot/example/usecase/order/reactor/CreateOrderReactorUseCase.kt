package com.karrot.example.usecase.order.reactor

import com.karrot.example.entity.order.Order
import com.karrot.example.repository.account.UserRxRepository
import com.karrot.example.repository.catalog.ProductReactorRepository
import com.karrot.example.repository.order.OrderFutureRepository
import com.karrot.example.repository.shipment.AddressReactiveRepository
import com.karrot.example.repository.store.StoreMutinyRepository
import com.karrot.example.usecase.order.CreateOrderUseCaseBase
import reactor.adapter.JdkFlowAdapter
import reactor.adapter.rxjava.RxJava3Adapter
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class CreateOrderReactorUseCase(
    private val userRepository: UserRxRepository,
    private val addressRepository: AddressReactiveRepository,
    private val productRepository: ProductReactorRepository,
    private val storeRepository: StoreMutinyRepository,
    private val orderRepository: OrderFutureRepository,
) : CreateOrderUseCaseBase() {
    data class InputValues(
        val userId: String,
        val productIds: List<String>,
    )

    fun execute(inputValues: InputValues): Mono<Order> {
        val (userId, productIds) = inputValues

        val createdOrder = RxJava3Adapter.maybeToMono(userRepository.findUserByIdAsMaybe(userId))
            .flatMap { buyer ->
                JdkFlowAdapter.flowPublisherToFlux(addressRepository.findAddressByUserAsPublisher(buyer))
                    .last()
                    .flatMap { address ->
                        isValidRegion(address)
                        productRepository.findAllProductsByIdsAsFlux(productIds)
                            .collectList()
                            .flatMap { products ->
                                check(products.isNotEmpty())
                                Flux.from(storeRepository.getStoresByProductsAsMulti(products))
                                    .collectList()
                                    .flatMap { stores ->
                                        check(stores.isNotEmpty())
                                        Mono.fromFuture(
                                            orderRepository.createOrderAsCompletableFuture(
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
