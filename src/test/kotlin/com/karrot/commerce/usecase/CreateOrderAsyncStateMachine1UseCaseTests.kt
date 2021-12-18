package com.karrot.commerce.usecase

import com.karrot.example.repository.account.UserRxRepository
import com.karrot.example.repository.catalog.ProductReactorRepository
import com.karrot.example.repository.order.OrderFutureRepository
import com.karrot.example.repository.shipment.AddressReactiveRepository
import com.karrot.example.repository.store.StoreMutinyRepository
import com.karrot.example.usecase.CreateOrderAsyncStateMachine1UseCase
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import org.apache.commons.lang3.time.StopWatch
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@ExtendWith(MockKExtension::class)
class CreateOrderAsyncStateMachine1UseCaseTests {
    @InjectMockKs
    private lateinit var createOrderUseCase: CreateOrderAsyncStateMachine1UseCase

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
    fun `should return a createdOrder in async with state machine`() {
        // given
        val userId = "user1"
        val productIds = listOf("product1", "product2", "product3")

        // when
        val watch = StopWatch().also { it.start() }
        val lock = CountDownLatch(1)

        val inputValues = CreateOrderAsyncStateMachine1UseCase.InputValues(userId, productIds)
        createOrderUseCase.execute(inputValues, { createdOrder ->
            watch.stop()
            lock.countDown()

            println("Time Elapsed: ${watch.time}ms")
            println(createdOrder)
        })

        // then
        lock.await(3000, TimeUnit.MILLISECONDS)
    }
}
