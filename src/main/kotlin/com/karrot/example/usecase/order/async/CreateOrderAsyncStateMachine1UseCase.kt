package com.karrot.example.usecase.order.async

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
import com.karrot.example.usecase.order.CreateOrderUseCaseBase
import com.karrot.example.vo.Address

class CreateOrderAsyncStateMachine1UseCase(
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

    class Continuation {
        var label: Int = 0
        lateinit var result: Any
        lateinit var buyer: User
        lateinit var address: Address
        lateinit var products: List<Product>
        lateinit var stores: List<Store>
        lateinit var order: Order
        lateinit var resume: () -> Unit

        fun resumeWith(result: Any) {
            this.result = result
            this.resume()
        }
    }

    fun execute(inputValues: InputValues, cb: (order: Order) -> Unit, continuation: Continuation? = null) {
        val (userId, productIds) = inputValues

        val cont = continuation ?: Continuation().apply {
            resume = fun() {
                this@CreateOrderAsyncStateMachine1UseCase.execute(inputValues, cb, this)
            }
        }

        when (cont.label) {
            0 -> {
                cont.label = 1
                userRepository.findUserByIdAsMaybe(userId)
                    .subscribe { user ->
                        cont.resumeWith(user)
                    }
            }
            1 -> {
                cont.label = 2
                cont.buyer = cont.result as User
                addressRepository.findAddressByUserAsPublisher(cont.buyer)
                    .subscribe(LastItemSubscriber { address ->
                        cont.resumeWith(address)
                    })
            }
            2 -> {
                cont.label = 3
                cont.address = cont.result as Address
                isValidRegion(cont.address)
                productRepository.findAllProductsByIdsAsFlux(productIds)
                    .collectList()
                    .subscribe { products ->
                        cont.resumeWith(products)
                    }
            }
            3 -> {
                cont.label = 4
                cont.products = cont.result as List<Product>
                check(cont.products.isNotEmpty())
                storeRepository.getStoresByProductsAsMulti(cont.products)
                    .collect().asList()
                    .subscribe().with { stores ->
                        cont.resumeWith(stores)
                    }
            }
            4 -> {
                cont.label = 5
                cont.stores = cont.result as List<Store>
                check(cont.stores.isNotEmpty())
                orderRepository.createOrderAsCompletableFuture(
                    cont.buyer, cont.products, cont.stores, cont.address
                ).whenComplete { order, _ ->
                    cont.resumeWith(order)
                }
            }
            5 -> {
                cont.order = cont.result as Order
                cb(cont.order)
            }
            else -> throw IllegalAccessException()
        }
    }
}
