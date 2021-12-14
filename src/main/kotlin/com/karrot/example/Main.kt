package com.karrot.example

import com.karrot.example.service.account.UserSyncService
import com.karrot.example.service.catalog.ProductSyncService
import com.karrot.example.service.order.OrderSyncService
import com.karrot.example.service.shipment.AddressSyncService
import com.karrot.example.service.store.StoreSyncService

fun main() {
    val userService = UserSyncService()
    val productService = ProductSyncService()
    val orderService = OrderSyncService()
    val addressService = AddressSyncService()
    val storeService = StoreSyncService()

    val productIds = listOf("product1", "product2", "product3")
    val userId = "user1"

    val buyer = userService.getUserByIdSync(userId)
    val address = addressService.getAddressByUserSync(buyer).last()
    val products = productService.getProductsByIdsSync(productIds)
    val stores = storeService.getStoresByProductsSync(products)

    val order = orderService.createOrderSync(buyer, products, stores, address)

    println(order)
}
