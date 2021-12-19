package com.karrot.example.usecase.order.coroutine

import com.karrot.example.entity.order.Order
import com.karrot.example.repository.account.UserAsyncRepository
import com.karrot.example.repository.catalog.ProductAsyncRepository
import com.karrot.example.repository.order.OrderAsyncRepository
import com.karrot.example.repository.shipment.AddressAsyncRepository
import com.karrot.example.repository.store.StoreAsyncRepository
import com.karrot.example.usecase.order.CreateOrderUseCaseBase
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.await
import kotlinx.coroutines.jdk9.awaitLast
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.rx3.awaitSingle

class CreateOrderCoroutineUseCase(
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

    suspend fun execute(inputValues: InputValues): Order {
        val (userId, productIds) = inputValues

        // 1. 구매자 조회
        val buyer = userRepository.findUserByIdAsMaybe(userId).awaitSingle()

        // 2. 주소 조회 및 유효성 체크
        val address = addressRepository.findAddressByUserAsPublisher(buyer).awaitLast()
        checkValidRegion(address)

        // 3. 상품들 조회
        val products = productRepository.findAllProductsByIdsAsFlux(productIds).asFlow().toList()
        check(products.isNotEmpty())

        // 4. 스토어 조회
        val stores = storeRepository.getStoresByProductsAsMulti(products).asFlow().toList()
        check(stores.isNotEmpty())

        // 5. 주문 생성
        val order = orderRepository.createOrderAsFuture(buyer, products, stores, address).await()

        return order
    }
}
