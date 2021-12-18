package com.karrot.example.usecase.order.coroutine

import com.karrot.example.entity.order.Order
import com.karrot.example.repository.account.UserRxRepository
import com.karrot.example.repository.catalog.ProductReactorRepository
import com.karrot.example.repository.order.OrderFutureRepository
import com.karrot.example.repository.shipment.AddressReactiveRepository
import com.karrot.example.repository.store.StoreMutinyRepository
import com.karrot.example.usecase.order.CreateOrderUseCaseBase
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.await
import kotlinx.coroutines.jdk9.awaitLast
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.rx3.awaitSingle

class CreateOrderCoroutineUseCase(
    private val userService: UserRxRepository,
    private val productService: ProductReactorRepository,
    private val orderService: OrderFutureRepository,
    private val addressService: AddressReactiveRepository,
    private val storeService: StoreMutinyRepository,
) : CreateOrderUseCaseBase() {
    data class InputValues(
        val userId: String,
        val productIds: List<String>,
    )

    suspend fun execute(inputValues: InputValues): Order {
        val (userId, productIds) = inputValues

        val buyer = userService.findUserByIdAsMaybe(userId).awaitSingle()
        val address = addressService.findAddressByUserAsPublisher(buyer).awaitLast()
        isValidRegion(address)
        val products = productService.findAllProductsByIdsAsFlux(productIds).collectList().awaitSingle()
        check(products.isNotEmpty())
        val stores = storeService.getStoresByProductsAsMulti(products).asFlow().toList()
        check(stores.isNotEmpty())
        val order = orderService.createOrderAsCompletableFuture(buyer, products, stores, address).await()

        return order
    }
}
