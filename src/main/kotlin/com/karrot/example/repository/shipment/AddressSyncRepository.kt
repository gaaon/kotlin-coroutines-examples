package com.karrot.example.repository.shipment

import com.karrot.example.entity.account.User
import com.karrot.example.vo.Address

class AddressSyncRepository : AddressRepositoryBase() {
    fun findAddressByUserSync(user: User): List<Address> {
        val address = prepareAddresses()
        Thread.sleep(500)
        return address
    }
}
