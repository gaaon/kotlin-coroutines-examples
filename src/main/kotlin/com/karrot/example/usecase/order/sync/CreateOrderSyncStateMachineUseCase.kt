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

    class SharedData {
        var label: Int = 0
        lateinit var result: Any
        lateinit var buyer: User
        lateinit var address: Address
        lateinit var products: List<Product>
        lateinit var stores: List<Store>
        lateinit var order: Order
        lateinit var resumeWith: (result: Any) -> Order
    }

    fun execute(
        inputValues: InputValues,
        sharedData: SharedData? = null,
    ): Order {
        val (userId, productIds) = inputValues

        val that = this
        val shared = sharedData ?: SharedData().apply {
            this.resumeWith = fun (result: Any): Order {
                this.result = result
                return that.execute(inputValues, this)
            }
        }

        return when (shared.label) {
            0 -> {
                shared.label = 1
                userRepository.findUserByIdSync(userId)
                    .let { user ->
                        shared.resumeWith(user)
                    }
            }
            1 -> {
                shared.label = 2
                shared.buyer = shared.result as User
                addressRepository.findAddressByUserSync(shared.buyer).last()
                    .let { address ->
                        shared.resumeWith(address)
                    }
            }
            2 -> {
                shared.label = 3
                shared.address = shared.result as Address
                checkValidRegion(shared.address)
                productRepository.findAllProductsByIdsSync(productIds)
                    .let { products ->
                        shared.resumeWith(products)
                    }
            }
            3 -> {
                shared.label = 4
                shared.products = shared.result as List<Product>
                check(shared.products.isNotEmpty())
                storeRepository.findStoresByProductsSync(shared.products)
                    .let { stores ->
                        shared.resumeWith(stores)
                    }
            }
            4 -> {
                shared.label = 5
                shared.stores = shared.result as List<Store>
                check(shared.stores.isNotEmpty())
                orderRepository.createOrderSync(
                    shared.buyer, shared.products, shared.stores, shared.address
                ).let { order ->
                    shared.resumeWith(order)
                }
            }
            5 -> {
                shared.order = shared.result as Order
                shared.order
            }
            else -> throw IllegalAccessException()
        }
    }
}
