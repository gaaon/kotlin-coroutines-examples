package com.karrot.example.usecase

import com.karrot.example.entity.order.Order
import com.karrot.example.repository.account.UserSyncRepository
import com.karrot.example.repository.catalog.ProductSyncRepository
import com.karrot.example.repository.order.OrderSyncRepository
import com.karrot.example.repository.shipment.AddressSyncRepository
import com.karrot.example.repository.store.StoreSyncRepository

class CreateOrderSyncUseCase(
    private val userRepository: UserSyncRepository,
    private val productRepository: ProductSyncRepository,
    private val storeRepository: StoreSyncRepository,
    private val orderRepository: OrderSyncRepository,
    private val addressRepository: AddressSyncRepository,
) : CreateOrderUseCaseBase() {
    data class InputValues(
        val userId: String,
        val productIds: List<String>,
    )

    fun execute(inputValues: InputValues): Order {
        val (userId, productIds) = inputValues

        val buyer = userRepository.findUserByIdSync(userId)
        val address = addressRepository.findAddressByUserSync(buyer).last()
        isValidRegion(address)
        val products = productRepository.findAllProductsByIdsSync(productIds)
        check(products.isNotEmpty())
        val stores = storeRepository.findStoresByProductsSync(products)
        check(stores.isNotEmpty())
        val order = orderRepository.createOrderSync(buyer, products, stores, address)

        return order
    }
}
