package com.karrot.commerce.usecase.order.sync

import com.karrot.example.repository.account.UserSyncRepository
import com.karrot.example.repository.catalog.ProductSyncRepository
import com.karrot.example.repository.order.OrderSyncRepository
import com.karrot.example.repository.shipment.AddressSyncRepository
import com.karrot.example.repository.store.StoreSyncRepository
import com.karrot.example.usecase.order.sync.CreateOrderSyncStateMachineUseCase
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import org.apache.commons.lang3.time.StopWatch
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CreateOrderSyncStateMachineUseCaseTests {
    @InjectMockKs
    private lateinit var createOrderUseCase: CreateOrderSyncStateMachineUseCase

    @SpyK
    private var spyUserRepository: UserSyncRepository = UserSyncRepository()

    @SpyK
    private var spyProductRepository: ProductSyncRepository = ProductSyncRepository()

    @SpyK
    private var spyStoreRepository: StoreSyncRepository = StoreSyncRepository()

    @SpyK
    private var spyOrderRepository: OrderSyncRepository = OrderSyncRepository()

    @SpyK
    private var spyAddressRepository: AddressSyncRepository = AddressSyncRepository()

    @Test
    fun `should return a createdOrder in sync with state machine`() {
        // given
        val userId = "user1"
        val productIds = listOf("product1", "product2", "product3")

        // when
        val watch = StopWatch().also { it.start() }

        val inputValues = CreateOrderSyncStateMachineUseCase.InputValues(userId, productIds)
        val createdOrder = createOrderUseCase.execute(inputValues)

        watch.stop()
        println("Time Elapsed: ${watch.time}ms")

        // then
        println(createdOrder)
    }
}
