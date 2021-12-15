package com.karrot.example

import com.karrot.example.service.catalog.ProductSyncService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun main() {
    val productService = ProductSyncService()

    runBlocking {
        val productIds = listOf("product1", "product2", "product3", "product4")

        val productsDefer = productIds.map { productId ->
            async {
                productService.getProductByIdSync(productId)
            }
        }

        val products = productsDefer.awaitAll()

        println(products)
    }
}
