package de.developkt.junit5demo

import java.time.OffsetDateTime
import kotlin.jvm.Throws

open class PostService(
    private val postDatabase: PostDatabase,
    private val userDatabase: UserDatabase,
    private val authDatabase: AuthDatabase,
    private val errorLogger: ErrorLogger
) {

    open fun createPost(userId: String?, post: Post?): Boolean {
        try {
            val user = userDatabase.loadUserById(userId)
            if (authDatabase.isAllowedToWritePosts(user)) {
                post?.createdAt = OffsetDateTime.now()
                post?.createdBy = user
                postDatabase.savePost(post)
                return true
            }
        } catch (ex: UserNotFoundException) {
            errorLogger.logError(
                Error(
                    "Could not load user $userId",
                    Error.ErrorType.DATABASE_ERROR
                )
            )
        }
        return false
    }
}

open class PostDatabase {
    open fun savePost(post: Post?) {
        // do nothing
    }
}

open class AuthDatabase {
    open fun isAllowedToWritePosts(user: User): Boolean = false // Implement logic
}

open class UserDatabase {

    @Throws(UserNotFoundException::class)
    open fun loadUserById(id: String?): User {
        return User("111", User.UserRole.READER, "John", "john@example.com")
    }
}

open class ErrorLogger {
    open fun logError(error: Error) {
        // something happens here
    }
}

data class Post(
    val subject: String,
    val message: String,
    var createdAt: OffsetDateTime?,
    var createdBy: User?
)

data class User(
    val userId: String,
    val role: UserRole,
    val name: String,
    val email: String
) {
    enum class UserRole {
        READER, WRITER, ADMIN;
    }
}

data class Error(
    val message: String,
    val type: ErrorType
) {
    enum class ErrorType {
        DATABASE_ERROR
    }
}

open class UserNotFoundException(override val message: String) : RuntimeException(message)