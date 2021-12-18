package com.karrot.example

import com.karrot.example.repository.catalog.ProductSyncRepository
import kotlinx.coroutines.runBlocking

fun main() {
    val productService = ProductSyncRepository()

    runBlocking {
        val productIds = listOf("product1", "product2", "product3", "product4")

        val products = productService.findAllProductsByIdsSync(productIds)

        println(products)
    }
}
