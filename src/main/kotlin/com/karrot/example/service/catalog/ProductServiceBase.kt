package com.karrot.example.service.catalog

import com.github.javafaker.Faker
import com.karrot.example.entity.catalog.Product
import com.karrot.example.vo.Money
import com.karrot.example.vo.MoneyCurrency
import java.util.*
import kotlin.random.Random

open class ProductServiceBase {
    private val faker = Faker()

    internal fun prepareProduct(productId: String): Product {
        val randomPriceAmount = Random.nextLong(1000, 10000)
        val randomStoreId = UUID.randomUUID().toString()

        return Product(
            id = productId,
            storeId = randomStoreId,
            name = faker.commerce().productName(),
            price = Money(MoneyCurrency.WON, randomPriceAmount)
        )
    }
}
