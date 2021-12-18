package com.karrot.example.repository.catalog

import com.karrot.example.const.TIME_DELAY_MS
import com.karrot.example.entity.catalog.Product

class ProductSyncRepository : ProductRepositoryBase() {
    fun findAllProductsByIdsSync(productIds: List<String>): List<Product> {
        Thread.sleep(TIME_DELAY_MS)
        return productIds.map { prepareProduct(it) }
    }

    fun findProductByIdSync(productId: String): Product {
        Thread.sleep(TIME_DELAY_MS/5)
        return prepareProduct(productId)
    }
}
