package com.karrot.example.usecase

import com.karrot.example.entity.account.User
import com.karrot.example.entity.catalog.Product
import com.karrot.example.entity.order.Order
import com.karrot.example.entity.store.Store
import com.karrot.example.repository.account.UserRxRepository
import com.karrot.example.repository.catalog.ProductReactorRepository
import com.karrot.example.repository.order.OrderFutureRepository
import com.karrot.example.repository.shipment.AddressReactiveRepository
import com.karrot.example.repository.store.StoreMutinyRepository
import com.karrot.example.util.awaitLast
import com.karrot.example.util.awaitSingle
import com.karrot.example.util.toList
import com.karrot.example.vo.Address
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

class CreateOrderAsyncStateMachine3UseCase(
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

    class UseCaseContinuation(
        private val continuation: Continuation<Any>,
    ) : Continuation<Any> {
        var label: Int = 0
        lateinit var result: Any
        lateinit var buyer: User
        lateinit var address: Address
        lateinit var products: List<Product>
        lateinit var stores: List<Store>
        lateinit var order: Order
        lateinit var resume: () -> Unit

        override val context: CoroutineContext = continuation.context

        override fun resumeWith(result: Result<Any>) {
            this.result = result
            this.resume()
        }

        fun complete(result: Result<Any>) {
            this.continuation.resumeWith(result)
        }
    }

    fun execute(inputValues: InputValues, continuation: Continuation<Any>) {
        val (userId, productIds) = inputValues

        val cont = continuation as? UseCaseContinuation ?: UseCaseContinuation(continuation).apply {
            resume = fun() {
                this@CreateOrderAsyncStateMachine3UseCase.execute(inputValues, this)
            }
        }

        when (cont.label) {
            0 -> {
                cont.label = 1
                userRepository.findUserByIdAsMaybe(userId).awaitSingle(cont)
            }
            1 -> {
                cont.label = 2
                cont.buyer = (cont.result as Result<User>).getOrThrow()
                addressRepository.findAddressByUserAsPublisher(cont.buyer).awaitLast(cont)
            }
            2 -> {
                cont.label = 3
                cont.address = (cont.result as Result<Address>).getOrThrow()
                isValidRegion(cont.address)
                productRepository.findAllProductsByIdsAsFlux(productIds).toList(cont)
            }
            3 -> {
                cont.label = 4
                cont.products = (cont.result as Result<List<Product>>).getOrThrow()
                check(cont.products.isNotEmpty())
                storeRepository.getStoresByProductsAsMulti(cont.products).toList(cont)
            }
            4 -> {
                cont.label = 5
                cont.stores = (cont.result as Result<List<Store>>).getOrThrow()
                check(cont.stores.isNotEmpty())
                orderRepository.createOrderAsCompletableFuture(
                    cont.buyer, cont.products, cont.stores, cont.address
                ).awaitSingle(cont)
            }
            5 -> {
                cont.order = (cont.result as Result<Order>).getOrThrow()
                cont.complete(Result.success(cont.order))
            }
            else -> throw IllegalAccessException()
        }
    }
}
