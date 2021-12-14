package com.karrot.example.service.account

import com.karrot.example.entity.account.User

class UserSyncService : UserServiceBase() {
    fun getUserByIdSync(userId: String): User {
        val user = prepareUser(userId)
        Thread.sleep(100)
        return user
    }
}
