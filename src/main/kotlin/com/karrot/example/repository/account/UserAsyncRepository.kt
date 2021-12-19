package com.karrot.example.repository.account

import com.karrot.example.entity.account.User
import io.reactivex.rxjava3.core.Maybe

interface UserAsyncRepository {
    fun findUserByIdAsMaybe(userId: String): Maybe<User>
}
