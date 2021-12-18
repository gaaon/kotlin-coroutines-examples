package com.karrot.example.usecase

import com.karrot.example.entity.order.Order
import com.karrot.example.repository.account.UserSyncRepository
import com.karrot.example.repository.catalog.ProductSyncRepository
import com.karrot.example.repository.order.OrderSyncRepository
import com.karrot.example.repository.shipment.AddressSyncRepository
import com.karrot.example.repository.store.StoreSyncRepository

class CreateOrderSync2UseCase(
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

    fun execute(inputValues: InputValues): Order {
        val (userId, productIds) = inputValues

        // 1
        val buyer = userRepository.findUserByIdSync(userId)

        // 2
        val address = addressRepository.findAddressByUserSync(buyer).last()
        isValidRegion(address)

        // 3
        val products = productRepository.findAllProductsByIdsSync(productIds)
        check(products.isNotEmpty())

        // 4
        val stores = storeRepository.findStoresByProductsSync(products)
        check(stores.isNotEmpty())

        // 5
        val order = orderRepository.createOrderSync(buyer, products, stores, address)

        return order
    }
}
