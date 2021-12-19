package com.karrot.example.repository.store

import com.karrot.example.entity.catalog.Product
import com.karrot.example.entity.store.Store
import io.smallrye.mutiny.Multi

class StoreMutinyRepository : StoreRepositoryBase(), StoreAsyncRepository {
    override fun getStoresByProductsAsMulti(products: List<Product>): Multi<Store> {
        return Multi.createFrom().iterable(
            products.map { prepareStore(it) }
        )
    }
}
