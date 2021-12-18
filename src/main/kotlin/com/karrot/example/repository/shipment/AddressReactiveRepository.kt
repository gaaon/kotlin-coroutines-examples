package com.karrot.example.repository.shipment

import com.karrot.example.entity.account.User
import com.karrot.example.vo.Address
import java.util.concurrent.Flow

class AddressReactiveRepository : AddressRepositoryBase() {
    fun findAddressByUserAsPublisher(user: User): Flow.Publisher<Address> {
        val addressIterator = prepareAddresses().iterator()

        return Flow.Publisher<Address> { subscriber ->
            subscriber.onSubscribe(object : Flow.Subscription {
                override fun request(n: Long) {
                    var cnt = n
                    while (cnt-- > 0) {
                        if (addressIterator.hasNext()) {
                            subscriber.onNext(addressIterator.next())
                        } else {
                            subscriber.onComplete()
                            break
                        }
                    }
                }

                override fun cancel() {
                    // do nothing
                }
            })
        }
    }
}
