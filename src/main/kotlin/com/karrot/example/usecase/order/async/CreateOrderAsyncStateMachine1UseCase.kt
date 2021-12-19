package com.karrot.example.usecase.order.async

import com.karrot.example.entity.account.User
import com.karrot.example.entity.catalog.Product
import com.karrot.example.entity.order.Order
import com.karrot.example.entity.store.Store
import com.karrot.example.repository.account.UserAsyncRepository
import com.karrot.example.repository.catalog.ProductAsyncRepository
import com.karrot.example.repository.order.OrderAsyncRepository
import com.karrot.example.repository.shipment.AddressAsyncRepository
import com.karrot.example.repository.shipment.LastItemSubscriber
import com.karrot.example.repository.store.StoreAsyncRepository
import com.karrot.example.usecase.order.CreateOrderUseCaseBase
import com.karrot.example.vo.Address

class CreateOrderAsyncStateMachine1UseCase(
    private val userRepository: UserAsyncRepository,
    private val addressRepository: AddressAsyncRepository,
    private val productRepository: ProductAsyncRepository,
    private val storeRepository: StoreAsyncRepository,
    private val orderRepository: OrderAsyncRepository,
) : CreateOrderUseCaseBase() {
    data class InputValues(
        val userId: String,
        val productIds: List<String>,
    )

    class SharedData {
        var label: Int = 0
        lateinit var result: Any
        lateinit var buyer: User
        lateinit var address: Address
        lateinit var products: List<Product>
        lateinit var stores: List<Store>
        lateinit var order: Order
        lateinit var resumeWith: (result: Any) -> Unit
    }

    fun execute(
        inputValues: InputValues,
        cb: (order: Order) -> Unit,
        sharedData: SharedData? = null,
    ) {
        val (userId, productIds) = inputValues

        val that = this
        val shared = sharedData ?: SharedData().apply {
            resumeWith = fun(result: Any) {
                this.result = result
                that.execute(inputValues, cb, this)
            }
        }

        when (shared.label) {
            0 -> {
                shared.label = 1
                userRepository.findUserByIdAsMaybe(userId)
                    .subscribe { user ->
                        shared.resumeWith(user)
                    }
            }
            1 -> {
                shared.label = 2
                shared.buyer = shared.result as User
                addressRepository.findAddressByUserAsPublisher(shared.buyer)
                    .subscribe(LastItemSubscriber { address ->
                        shared.resumeWith(address)
                    })
            }
            2 -> {
                shared.label = 3
                shared.address = shared.result as Address
                checkValidRegion(shared.address)
                productRepository.findAllProductsByIdsAsFlux(productIds)
                    .collectList()
                    .subscribe { products ->
                        shared.resumeWith(products)
                    }
            }
            3 -> {
                shared.label = 4
                shared.products = shared.result as List<Product>
                check(shared.products.isNotEmpty())
                storeRepository.getStoresByProductsAsMulti(shared.products)
                    .collect().asList()
                    .subscribe().with { stores ->
                        shared.resumeWith(stores)
                    }
            }
            4 -> {
                shared.label = 5
                shared.stores = shared.result as List<Store>
                check(shared.stores.isNotEmpty())
                orderRepository.createOrderAsFuture(
                    shared.buyer, shared.products, shared.stores, shared.address
                ).whenComplete { order, _ ->
                    shared.resumeWith(order)
                }
            }
            5 -> {
                shared.order = shared.result as Order
                cb(shared.order)
            }
            else -> throw IllegalAccessException()
        }
    }
}
