package com.karrot.example.usecase

import com.karrot.example.entity.account.User
import com.karrot.example.entity.catalog.Product
import com.karrot.example.entity.order.Order
import com.karrot.example.entity.store.Store
import com.karrot.example.repository.account.UserSyncRepository
import com.karrot.example.repository.catalog.ProductSyncRepository
import com.karrot.example.repository.order.OrderSyncRepository
import com.karrot.example.repository.shipment.AddressSyncRepository
import com.karrot.example.repository.store.StoreSyncRepository
import com.karrot.example.vo.Address

class CreateOrderSyncStateUseCase(
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

        var label = 0

        lateinit var buyer: User
        lateinit var address: Address
        lateinit var products: List<Product>
        lateinit var stores: List<Store>
        lateinit var order: Order

        fun resumeWith(value: Any): Order {
            when (label) {
                0 -> {
                    label++
                    val _buyer = userRepository.findUserByIdSync(userId)
                    return resumeWith(_buyer)
                }
                1 -> {
                    label++
                    buyer = value as User
                    val _address = addressRepository.findAddressByUserSync(buyer).last()
                    return resumeWith(_address)
                }
                2 -> {
                    label++
                    address = value as Address
                    isValidRegion(address)
                    val _products = productRepository.findAllProductsByIdsSync(productIds)
                    return resumeWith(_products)
                }
                3 -> {
                    label++
                    products = value as List<Product>
                    check(products.isNotEmpty())
                    val _stores = storeRepository.findStoresByProductsSync(products)
                    return resumeWith(_stores)
                }
                4 -> {
                    label++
                    stores = value as List<Store>
                    check(stores.isNotEmpty())
                    val _order = orderRepository.createOrderSync(buyer, products, stores, address)
                    return resumeWith(_order)
                }
                5 -> {
                    order = value as Order
                    return order
                }
                else -> throw IllegalAccessException()
            }
        }

        return resumeWith(Unit)
    }
}
