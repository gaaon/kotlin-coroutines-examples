package com.karrot.example.usecase.order

import com.karrot.example.vo.Address

open class CreateOrderUseCaseBase {
    fun isValidRegion(address: Address) = true
}
