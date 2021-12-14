package com.karrot.example.service.store

import com.karrot.example.entity.catalog.Product
import com.karrot.example.entity.store.Store

class StoreSyncService : StoreServiceBase() {
    fun getStoresByProductsSync(products: List<Product>): List<Store> {
        Thread.sleep(100)
        return products.map { prepareStore(it) }
    }
}
