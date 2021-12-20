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
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

class CreateOrderAsyncStateMachine2UseCase(
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

    class SharedDataContinuation(
        val completion: Continuation<Any>,
    ) : Continuation<Any> {
        var label: Int = 0
        lateinit var result: Any
        lateinit var buyer: User
        lateinit var address: Address
        lateinit var products: List<Product>
        lateinit var stores: List<Store>
        lateinit var order: Order
        lateinit var resume: () -> Unit

        override val context: CoroutineContext = completion.context
        override fun resumeWith(result: Result<Any>) {
            this.result = result
            this.resume()
        }
    }

    fun execute(inputValues: InputValues, completion: Continuation<Any>) {
        val (userId, productIds) = inputValues

        val that = this
        val cont = completion as? SharedDataContinuation
            ?: SharedDataContinuation(completion).apply {
                resume = fun() {
                    // recursive self
                    that.execute(inputValues, this)
                }
            }

        when (cont.label) {
            0 -> {
                cont.label = 1
                userRepository.findUserByIdAsMaybe(userId)
                    .subscribe { user ->
                        cont.resumeWith(Result.success(user))
                    }
            }
            1 -> {
                cont.label = 2
                cont.buyer = (cont.result as Result<User>).getOrThrow()
                addressRepository.findAddressByUserAsPublisher(cont.buyer)
                    .subscribe(LastItemSubscriber { address ->
                        cont.resumeWith(Result.success(address))
                    })
            }
            2 -> {
                cont.label = 3
                cont.address = (cont.result as Result<Address>).getOrThrow()
                checkValidRegion(cont.address)
                productRepository.findAllProductsByIdsAsFlux(productIds)
                    .collectList()
                    .subscribe { products ->
                        cont.resumeWith(Result.success(products))
                    }
            }
            3 -> {
                cont.label = 4
                cont.products = (cont.result as Result<List<Product>>).getOrThrow()
                check(cont.products.isNotEmpty())
                storeRepository.getStoresByProductsAsMulti(cont.products)
                    .collect().asList()
                    .subscribe().with { stores ->
                        cont.resumeWith(Result.success(stores))
                    }
            }
            4 -> {
                cont.label = 5
                cont.stores = (cont.result as Result<List<Store>>).getOrThrow()
                check(cont.stores.isNotEmpty())
                orderRepository.createOrderAsFuture(
                    cont.buyer, cont.products, cont.stores, cont.address
                ).whenComplete { order, _ ->
                    cont.resumeWith(Result.success(order))
                }
            }
            5 -> {
                cont.order = (cont.result as Result<Order>).getOrThrow()
                cont.completion.resumeWith(Result.success(cont.order))
            }
            else -> throw IllegalAccessException()
        }
    }
}
