package com.karrot.example.repository.store

import com.github.javafaker.Faker
import com.karrot.example.entity.catalog.Product
import com.karrot.example.entity.store.Store

open class StoreRepositoryBase {
    private val faker = Faker()

    internal fun prepareStore(product: Product): Store {
        val randomStoreName = faker.company().name()

        return Store(
            id = product.storeId,
            name = randomStoreName,
        )
    }
}
