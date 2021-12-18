package com.karrot.example.repository.account

import com.karrot.example.entity.account.User
import io.reactivex.rxjava3.core.Maybe
import java.util.concurrent.TimeUnit

class UserRxRepository : UserRepositoryBase() {
    fun findUserByIdAsMaybe(userId: String): Maybe<User> {
        val user = prepareUser(userId)
        return Maybe.just(user)
            .delay(500L, TimeUnit.MILLISECONDS)
    }
}
