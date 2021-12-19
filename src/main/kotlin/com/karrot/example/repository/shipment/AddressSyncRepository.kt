package com.karrot.example.repository.shipment

import com.karrot.example.const.TIME_DELAY_MS
import com.karrot.example.entity.account.User
import com.karrot.example.vo.Address

class AddressSyncRepository : AddressRepositoryBase() {
    @Suppress
    fun findAddressByUserSync(@Suppress("UNUSED_PARAMETER") user: User): List<Address> {
        val address = prepareAddresses()
        Thread.sleep(TIME_DELAY_MS)
        return address
    }
}
