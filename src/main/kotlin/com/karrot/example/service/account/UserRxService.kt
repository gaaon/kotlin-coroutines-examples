package com.karrot.example.service.account

import com.karrot.example.entity.account.User
import io.reactivex.rxjava3.core.Maybe
import java.util.concurrent.TimeUnit

class UserRxService : UserServiceBase() {
    fun getUserByIdAsMaybe(userId: String): Maybe<User> {
        val user = prepareUser(userId)
        return Maybe.just(user)
            .delay(100L, TimeUnit.MILLISECONDS)
    }
}
