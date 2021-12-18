package com.karrot.example.repository.account

import com.github.javafaker.Faker
import com.karrot.example.entity.account.User

open class UserRepositoryBase {
    private val faker = Faker()

    internal fun prepareUser(userId: String): User {
        val name = faker.name().fullName()

        return User(
            id = userId,
            name = name,
        )
    }
}
