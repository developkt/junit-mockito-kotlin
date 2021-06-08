package de.developkt.junit5demo

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.kotlin.*
import java.util.*

internal class PostServiceTest {

    private lateinit var postService: PostService

    private lateinit var postDatabase: PostDatabase
    private lateinit var userDatabase: UserDatabase
    private lateinit var authDatabase: AuthDatabase
    private lateinit var errorLogger: ErrorLogger

    @BeforeEach
    fun setUp() {
        postDatabase = mock()
        userDatabase = mock()
        authDatabase = mock()
        errorLogger = mock()

        postService = PostService(postDatabase, userDatabase, authDatabase, errorLogger)
    }

    @Test
    fun `should create non null instances of service and mocks`() {
        assertNotNull(postService)
        assertNotNull(postDatabase)
        assertNotNull(userDatabase)
        assertNotNull(authDatabase)
        assertNotNull(errorLogger)
    }

    @Test
    fun `should allow a user with role writer to create posts`() {
        val userId = "111"
        val writer = User(
            userId, User.UserRole.WRITER, "John", "john@example.com"
        )
        val post = Post(
            "Hello", "Hello World post", null, null
        )

        `when`(userDatabase.loadUserById(userId)).thenReturn(writer)
        whenever(authDatabase.isAllowedToWritePosts(writer)).thenReturn(true)

        val result = postService.createPost(userId, post)

        assertTrue(result)
        assertEquals(writer, post.createdBy)
        verify(userDatabase).loadUserById(userId)
        verify(authDatabase).isAllowedToWritePosts(writer)
        verify(postDatabase).savePost(post)
    }

    @Test
    fun `should prohibit a user with role reader to create posts`() {
        val reader = User(
            UUID.randomUUID().toString(), User.UserRole.READER, "John", "john@example.com"
        )
        val post = Post("Hello", "World", null, null)

        whenever(userDatabase.loadUserById(anyString())).thenReturn(reader)
        whenever(authDatabase.isAllowedToWritePosts(any())).thenReturn(false)

        val result = postService.createPost("", post)

        assertFalse(result)
        assertNull(post.createdBy)
        assertNull(post.createdAt)
        verify(userDatabase).loadUserById(anyString())
        verify(authDatabase).isAllowedToWritePosts(any())
        verifyNoMoreInteractions(userDatabase, authDatabase)
        verifyZeroInteractions(postDatabase)
    }

    @Test
    fun `should log an error when user not found`() {
        val post = Post("Hello", "World", null, null)
        doThrow(UserNotFoundException("user not found")).`when`(userDatabase).loadUserById(any())
        val captor = argumentCaptor<Error>()
        val userId = UUID.randomUUID().toString()

        val result = postService.createPost(userId, post)

        assertFalse(result)
        verify(errorLogger).logError(captor.capture())
        assertEquals(Error.ErrorType.DATABASE_ERROR, captor.firstValue.type)
        assertEquals("Could not load user $userId", captor.firstValue.message)
    }

}