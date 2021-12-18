package com.karrot.example.repository.account

import com.karrot.example.entity.account.User

class UserSyncRepository : UserRepositoryBase() {
    fun findUserByIdSync(userId: String): User {
        val user = prepareUser(userId)
        Thread.sleep(500)
        return user
    }
}
