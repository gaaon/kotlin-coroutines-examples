package com.karrot.example.service.shipment

import com.karrot.example.entity.account.User
import com.karrot.example.vo.Address

class AddressSyncService : AddressServiceBase() {
    fun getAddressByUserSync(user: User): List<Address> {
        return prepareAddresses()
    }
}
