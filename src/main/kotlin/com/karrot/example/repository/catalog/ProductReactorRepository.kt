package com.karrot.example.repository.catalog

import com.karrot.example.const.TIME_DELAY_MS
import com.karrot.example.entity.catalog.Product
import reactor.core.publisher.Flux
import java.time.Duration

class ProductReactorRepository : ProductRepositoryBase(), ProductAsyncRepository {
    override fun findAllProductsByIdsAsFlux(productIds: List<String>): Flux<Product> {
        val products = productIds.map { prepareProduct(it) }
        return Flux.fromIterable(products)
            .delayElements(Duration.ofMillis(TIME_DELAY_MS))
    }
}
