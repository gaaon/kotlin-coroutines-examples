package com.karrot.example

import com.karrot.example.service.catalog.ProductSyncService
import kotlinx.coroutines.runBlocking

fun main() {
    val productService = ProductSyncService()

    runBlocking {
        val productIds = listOf("product1", "product2", "product3", "product4")

        val products = productService.getProductsByIdsSync(productIds)

        println(products)
    }
}
