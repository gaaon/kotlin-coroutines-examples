package com.karrot.example.service.catalog

import com.karrot.example.entity.catalog.Product

class ProductSyncService : ProductServiceBase() {
    fun getProductsByIdsSync(productIds: List<String>): List<Product> {
        Thread.sleep(100)
        return productIds.map { prepareProduct(it) }
    }
}
