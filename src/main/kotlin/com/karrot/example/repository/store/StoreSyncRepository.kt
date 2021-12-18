package com.karrot.example.repository.store

import com.karrot.example.const.TIME_DELAY_MS
import com.karrot.example.entity.catalog.Product
import com.karrot.example.entity.store.Store

class StoreSyncRepository : StoreRepositoryBase() {
    fun findStoresByProductsSync(products: List<Product>): List<Store> {
        return products.map { prepareStore(it) }
    }
}
