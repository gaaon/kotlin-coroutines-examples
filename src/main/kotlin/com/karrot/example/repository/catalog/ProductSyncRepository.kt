package com.karrot.example.repository.catalog

import com.karrot.example.entity.catalog.Product

class ProductSyncRepository : ProductRepositoryBase() {
    fun findAllProductsByIdsSync(productIds: List<String>): List<Product> {
        Thread.sleep(500)
        return productIds.map { prepareProduct(it) }
    }

    fun findProductByIdSync(productId: String): Product {
        Thread.sleep(100)
        return prepareProduct(productId)
    }
}
