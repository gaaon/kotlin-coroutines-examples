package com.karrot.example.service.catalog

import com.karrot.example.entity.catalog.Product
import reactor.core.publisher.Flux
import java.time.Duration

class ProductReactorService : ProductServiceBase() {
    fun getProductsByIdsAsFlux(productIds: List<String>): Flux<Product> {
        val products = productIds.map { prepareProduct(it) }
        return Flux.fromIterable(products)
            .delayElements(Duration.ofMillis(100))
    }
}
