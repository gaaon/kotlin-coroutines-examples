package com.karrot.example.repository.shipment

import com.karrot.example.vo.Address
import java.util.concurrent.Flow

class AddressLastItemSubscriber(
    private val callback: (address: Address) -> Unit
) : Flow.Subscriber<Address> {
    lateinit var s: Flow.Subscription
    var address: Address? = null

    override fun onNext(t: Address) {
        address = t
        this.s.request(1)
    }

    override fun onSubscribe(s: Flow.Subscription) {
        this.s = s
        this.s.request(1)
    }

    override fun onError(t: Throwable) {
        // do nothing
    }

    override fun onComplete() {
        checkNotNull(address)
        callback.invoke(address!!)
    }
}
