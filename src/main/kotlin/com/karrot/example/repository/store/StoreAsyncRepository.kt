package com.karrot.example.repository.store

import com.karrot.example.entity.catalog.Product
import com.karrot.example.entity.store.Store
import io.smallrye.mutiny.Multi

interface StoreAsyncRepository {
    fun findStoresByProductsAsMulti(products: List<Product>): Multi<Store>
}
