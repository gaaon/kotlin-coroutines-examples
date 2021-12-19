package com.karrot.example.repository.catalog

import com.karrot.example.entity.catalog.Product
import reactor.core.publisher.Flux

interface ProductAsyncRepository {
    fun findAllProductsByIdsAsFlux(productIds: List<String>): Flux<Product>
}
