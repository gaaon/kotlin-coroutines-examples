package com.karrot.example.repository.account

import com.karrot.example.const.TIME_DELAY_MS
import com.karrot.example.entity.account.User
import io.reactivex.rxjava3.core.Maybe
import java.util.concurrent.TimeUnit

class UserRxRepository : UserRepositoryBase(), UserAsyncRepository {
    override fun findUserByIdAsMaybe(userId: String): Maybe<User> {
        val user = prepareUser(userId)
        return Maybe.just(user)
            .delay(TIME_DELAY_MS, TimeUnit.MILLISECONDS)
    }
}
