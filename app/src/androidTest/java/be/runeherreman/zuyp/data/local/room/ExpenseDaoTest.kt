package be.runeherreman.zuyp.data.local.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.runeherreman.zuyp.data.local.room.dao.ExpenseDao
import be.runeherreman.zuyp.data.local.room.database.AppDatabase
import be.runeherreman.zuyp.data.local.room.entity.expenses.ExpenseEntity
import be.runeherreman.zuyp.data.local.room.entity.expenses.ExpenseShareEntity
import be.runeherreman.zuyp.data.local.room.entity.hangouts.HangoutEntity
import be.runeherreman.zuyp.data.local.room.entity.users.UserEntity
import be.runeherreman.zuyp.ui.hangout.HangoutEvent
import com.mapbox.maps.extension.style.types.TerrainDsl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class ExpenseDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: ExpenseDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        dao = db.expenseDao()
    }

    @After
    fun closeDb() = db.close()

    @Test
    fun insertExpenseWithShares_thenReadBack() = runBlocking {
        val userId = UUID.randomUUID()
        val hangoutId = UUID.randomUUID()
        val expenseId = UUID.randomUUID()

        val user = UserEntity(userId, "Alice", LocalDate.now(), "alice@text.com")
        db.userDao().insertUser(user)

        val hangout = HangoutEntity(hangoutId, "Test Hangout", "", "Bruges", 51.2, 3.2, LocalDateTime.now(), LocalDateTime.now().plusHours(2), userId, false)
        db.hangoutDao().insert(hangout)

        val expense = ExpenseEntity(expenseId, hangoutId, userId, "Pizza", 20.0, null, LocalDateTime.now())
        val share = ExpenseShareEntity(expenseId, userId, 20.0)
        dao.insertExpenseWithShares(expense, listOf(share))

        val result = dao.getExpensesForHangout(hangoutId).first()

        assertEquals(1, result.size)
        assertEquals("Pizza", result[0].expense.title)
        assertEquals(20.0, result[0].expense.amount, 0.001)
        assertEquals(1, result[0].shares.size)
        assertEquals(20.0, result[0].shares[0].shareAmount, 0.001)    }
}