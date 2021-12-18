package com.karrot.example.repository.shipment

import java.util.concurrent.Flow

class LastItemSubscriber<T>(
    private val callback: (result: T) -> Unit
) : Flow.Subscriber<T> {
    lateinit var s: Flow.Subscription
    var result: T? = null

    override fun onNext(t: T) {
        result = t
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
        checkNotNull(result)
        callback.invoke(result!!)
    }
}
