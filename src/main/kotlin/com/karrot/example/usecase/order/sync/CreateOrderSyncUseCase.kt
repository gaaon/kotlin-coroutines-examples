package com.karrot.example.usecase.order.sync

import com.karrot.example.entity.order.Order
import com.karrot.example.repository.account.UserSyncRepository
import com.karrot.example.repository.catalog.ProductSyncRepository
import com.karrot.example.repository.order.OrderSyncRepository
import com.karrot.example.repository.shipment.AddressSyncRepository
import com.karrot.example.repository.store.StoreSyncRepository
import com.karrot.example.usecase.order.CreateOrderUseCaseBase

class CreateOrderSyncUseCase(
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

        // 1. 구매자 조회
        val buyer = userRepository.findUserByIdSync(userId)

        // 2. 주소 조회 및 유효성 체크
        val address = addressRepository.findAddressByUserSync(buyer).last()
        checkValidRegion(address)

        // 3. 상품들 조회
        val products = productRepository.findAllProductsByIdsSync(productIds)
        check(products.isNotEmpty())

        // 4. 스토어 조회
        val stores = storeRepository.findStoresByProductsSync(products)
        check(stores.isNotEmpty())

        // 5. 주문 생성
        val order = orderRepository.createOrderSync(buyer, products, stores, address)

        return order
    }
}
