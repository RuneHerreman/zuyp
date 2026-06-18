package be.runeherreman.zuyp.domain.usecases.expenses

import be.runeherreman.zuyp.domain.model.AttendanceStatus
import be.runeherreman.zuyp.domain.model.Expense
import be.runeherreman.zuyp.domain.model.PersonBalance
import be.runeherreman.zuyp.domain.model.User
import be.runeherreman.zuyp.domain.repository.ExpenseRepository
import be.runeherreman.zuyp.fakes.FakeExpenseRepository
import io.mockk.coVerify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.exp

class AddExpenseUseCaseTest {
    @Test
    fun invoke_forwardsExpenseToRepository() = runTest { // because of suspend function
        val repo = FakeExpenseRepository()
        val useCase = AddExpenseUseCase(repo)
        val expense = Expense(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Test",
            10.0,
            User(
                UUID.randomUUID(),
                "Test",
                LocalDate.now(),
                "Test",
                "Test",
                AttendanceStatus.NOT_INTERESTED
            ),
            imageUri = "test",
            createdAt = LocalDateTime.now(),
            shares = emptyList(),
        )

        useCase(expense)

        assertEquals(1, repo.added.size)
        assertEquals(expense, repo.added.first())
    }
}