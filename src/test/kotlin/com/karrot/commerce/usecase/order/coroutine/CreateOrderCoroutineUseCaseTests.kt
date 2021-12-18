package com.karrot.commerce.usecase.order.coroutine

import com.karrot.example.repository.account.UserRxRepository
import com.karrot.example.repository.catalog.ProductReactorRepository
import com.karrot.example.repository.order.OrderFutureRepository
import com.karrot.example.repository.shipment.AddressReactiveRepository
import com.karrot.example.repository.store.StoreMutinyRepository
import com.karrot.example.usecase.order.coroutine.CreateOrderCoroutineUseCase
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.apache.commons.lang3.time.StopWatch
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CreateOrderCoroutineUseCaseTests {
    @InjectMockKs
    private lateinit var createOrderUseCase: CreateOrderCoroutineUseCase

    @SpyK
    private var spyUserRepository: UserRxRepository = UserRxRepository()

    @SpyK
    private var spyProductRepository: ProductReactorRepository = ProductReactorRepository()

    @SpyK
    private var spyStoreRepository: StoreMutinyRepository = StoreMutinyRepository()

    @SpyK
    private var spyOrderRepository: OrderFutureRepository = OrderFutureRepository()

    @SpyK
    private var spyAddressRepository: AddressReactiveRepository = AddressReactiveRepository()

    @Test
    fun `should return a createdOrder in coroutine`() = runBlocking {
        // given
        val userId = "user1"
        val productIds = listOf("product1", "product2", "product3")

        // when
        val watch = StopWatch().also { it.start() }

        val inputValues = CreateOrderCoroutineUseCase.InputValues(userId, productIds)
        val createdOrder = createOrderUseCase.execute(inputValues)

        watch.stop()
        println("Time Elapsed: ${watch.time}ms")

        // then
        println(createdOrder)
    }
}
