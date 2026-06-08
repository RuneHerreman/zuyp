package be.runeherreman.zuyp.data.local.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.runeherreman.zuyp.data.local.room.database.AppDatabase
import be.runeherreman.zuyp.data.local.room.entity.users.FriendshipEntity
import be.runeherreman.zuyp.data.local.room.entity.users.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() = db.close()

    @Test
    fun insertUser_thenGetById_returnsUser() = runBlocking {
        val id = UUID.randomUUID()
        db.userDao().insertUser(UserEntity(id, "Alice", LocalDate.now(), "alice@test.com"))

        val result = db.userDao().getUserById(id)

        assertNotNull(result)
        assertEquals("Alice", result!!.name)
    }

    @Test
    fun getAllUsers_afterInsert_returnsAllAsFlow() = runBlocking {
        db.userDao().insertUser(UserEntity(UUID.randomUUID(), "Bob", LocalDate.now(), "bob@test.com"))
        db.userDao().insertUser(UserEntity(UUID.randomUUID(), "Carol", LocalDate.now(), "carol@test.com"))

        val users = db.userDao().getAllUsers().first()

        assertEquals(2, users.size)
    }

    @Test
    fun deleteUser_removesFromFlow() = runBlocking {
        val id = UUID.randomUUID()
        db.userDao().insertUser(UserEntity(id, "Dave", LocalDate.now(), "dave@test.com"))

        db.userDao().deleteUserById(id)

        val users = db.userDao().getAllUsers().first()
        assertTrue(users.none { it.id == id })
    }

    @Test
    fun getUserById_nonExistent_returnsNull() = runBlocking {
        assertNull(db.userDao().getUserById(UUID.randomUUID()))
    }

    @Test
    fun addFriendship_areFriends_returnsTrue() = runBlocking {
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        db.userDao().insertUsers(
            listOf(
                UserEntity(id1, "Eve", LocalDate.now(), "eve@test.com"),
                UserEntity(id2, "Frank", LocalDate.now(), "frank@test.com"),
            )
        )

        val (first, second) = if (id1 < id2) id1 to id2 else id2 to id1
        db.userDao().addFriendship(FriendshipEntity(first, second))

        assertTrue(db.userDao().areFriends(id1, id2))
    }

    @Test
    fun removeFriendship_areFriends_returnsFalse() = runBlocking {
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        db.userDao().insertUsers(
            listOf(
                UserEntity(id1, "Grace", LocalDate.now(), "grace@test.com"),
                UserEntity(id2, "Hank", LocalDate.now(), "hank@test.com"),
            )
        )
        val (first, second) = if (id1 < id2) id1 to id2 else id2 to id1
        db.userDao().addFriendship(FriendshipEntity(first, second))

        db.userDao().removeFriendship(id1, id2)

        assertFalse(db.userDao().areFriends(id1, id2))
    }
}
