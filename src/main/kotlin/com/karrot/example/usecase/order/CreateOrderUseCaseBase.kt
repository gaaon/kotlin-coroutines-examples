package com.karrot.example.usecase.order

import com.karrot.example.vo.Address

open class CreateOrderUseCaseBase {
    fun checkValidRegion(address: Address) = true
}
