package com.karrot.example.service.shipment

import com.karrot.example.vo.Address

open class AddressServiceBase {
    internal fun prepareAddresses(): List<Address> {
        return listOf(
            Address(
                roadNameAddress = "서울특별시 중구 세종대로 110",
                detailAddress = "1층",
            ),
            Address(
                roadNameAddress = "서울특별시 서초구 강남대로 465",
                detailAddress = "10층 B",
            ),
        )
    }
}
