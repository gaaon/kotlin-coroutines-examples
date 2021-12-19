package com.karrot.example.usecase.order.sync

import com.karrot.example.entity.account.User
import com.karrot.example.entity.catalog.Product
import com.karrot.example.entity.order.Order
import com.karrot.example.entity.store.Store
import com.karrot.example.repository.account.UserSyncRepository
import com.karrot.example.repository.catalog.ProductSyncRepository
import com.karrot.example.repository.order.OrderSyncRepository
import com.karrot.example.repository.shipment.AddressSyncRepository
import com.karrot.example.repository.store.StoreSyncRepository
import com.karrot.example.usecase.order.CreateOrderUseCaseBase
import com.karrot.example.vo.Address

class CreateOrderSyncStateMachineUseCase(
    private val userRepository: UserSyncRepository,
    private val addressRepository: AddressSyncRepository,
    private val productRepository: ProductSyncRepository,
    private val storeRepository: StoreSyncRepository,
    private val orderRepository: OrderSyncRepository,
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
        lateinit var resume: () -> Order

        fun resumeWith(result: Any): Order {
            this.result = result
            return this.resume()
        }
    }

    fun execute(inputValues: InputValues, continuation: Continuation? = null): Order {
        val (userId, productIds) = inputValues

        val cont = continuation ?: Continuation().apply {
            resume = fun (): Order {
                return this@CreateOrderSyncStateMachineUseCase.execute(inputValues, this)
            }
        }

        return when (cont.label) {
            0 -> {
                cont.label = 1
                userRepository.findUserByIdSync(userId)
                    .let { user ->
                        cont.resumeWith(user)
                    }
            }
            1 -> {
                cont.label = 2
                cont.buyer = cont.result as User
                addressRepository.findAddressByUserSync(cont.buyer).last()
                    .let { address ->
                        cont.resumeWith(address)
                    }
            }
            2 -> {
                cont.label = 3
                cont.address = cont.result as Address
                checkValidRegion(cont.address)
                productRepository.findAllProductsByIdsSync(productIds)
                    .let { products ->
                        cont.resumeWith(products)
                    }
            }
            3 -> {
                cont.label = 4
                cont.products = cont.result as List<Product>
                check(cont.products.isNotEmpty())
                storeRepository.findStoresByProductsSync(cont.products)
                    .let { stores ->
                        cont.resumeWith(stores)
                    }
            }
            4 -> {
                cont.label = 5
                cont.stores = cont.result as List<Store>
                check(cont.stores.isNotEmpty())
                orderRepository.createOrderSync(
                    cont.buyer, cont.products, cont.stores, cont.address
                ).let { order ->
                    cont.resumeWith(order)
                }
            }
            5 -> {
                cont.order = cont.result as Order
                cont.order
            }
            else -> throw IllegalAccessException()
        }
    }
}
