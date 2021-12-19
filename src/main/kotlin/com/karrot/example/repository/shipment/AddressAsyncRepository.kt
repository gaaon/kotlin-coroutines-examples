package com.karrot.example.repository.shipment

import com.karrot.example.entity.account.User
import com.karrot.example.vo.Address
import java.util.concurrent.Flow

interface AddressAsyncRepository {
    fun findAddressByUserAsPublisher(user: User): Flow.Publisher<Address>
}
