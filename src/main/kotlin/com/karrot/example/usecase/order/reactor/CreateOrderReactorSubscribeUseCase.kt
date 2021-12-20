package com.karrot.example.usecase.order.reactor

import com.karrot.example.entity.order.Order
import com.karrot.example.repository.account.UserRxRepository
import com.karrot.example.repository.catalog.ProductReactorRepository
import com.karrot.example.repository.order.OrderFutureRepository
import com.karrot.example.repository.shipment.AddressReactiveRepository
import com.karrot.example.repository.shipment.LastItemSubscriber
import com.karrot.example.repository.store.StoreMutinyRepository
import com.karrot.example.usecase.order.CreateOrderUseCaseBase
import reactor.core.publisher.Mono

class CreateOrderReactorSubscribeUseCase(
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

        return Mono.create { emitter ->
            userRepository.findUserByIdAsMaybe(userId)
                .subscribe { buyer ->
                    addressRepository.findAddressByUserAsPublisher(buyer)
                        .subscribe(LastItemSubscriber { address ->
                            checkValidRegion(address)
                            productRepository.findAllProductsByIdsAsFlux(productIds)
                                .collectList()
                                .subscribe { products ->
                                    check(products.isNotEmpty())
                                    storeRepository.findStoresByProductsAsMulti(products)
                                        .collect().asList()
                                        .subscribe().with { stores ->
                                            check(stores.isNotEmpty())
                                            orderRepository.createOrderAsFuture(
                                                buyer, products, stores, address
                                            ).whenComplete { order, _ ->
                                                emitter.success(order)
                                            }
                                        }
                                }
                        })
                }
        }
    }
}
