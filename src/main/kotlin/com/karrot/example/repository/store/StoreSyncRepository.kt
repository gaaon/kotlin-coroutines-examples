package com.karrot.example.repository.store

import com.karrot.example.entity.catalog.Product
import com.karrot.example.entity.store.Store

class StoreSyncRepository : StoreRepositoryBase() {
    fun findStoresByProductsSync(products: List<Product>): List<Store> {
        Thread.sleep(100)
        return products.map { prepareStore(it) }
    }
}
