package be.runeherreman.zuyp.domain.usecases.hangouts

import be.runeherreman.zuyp.domain.model.AttendanceStatus
import be.runeherreman.zuyp.fakes.FakeHangoutRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class UpdateAttendanceUseCaseTest {

    @Test
    fun invoke_withStatus_callsUpdateOnRepository() = runTest {
        val repo = FakeHangoutRepository()
        val useCase = UpdateAttendanceUseCase(repo)
        val hangoutId = UUID.randomUUID()
        val userId = UUID.randomUUID()

        useCase(hangoutId, userId, AttendanceStatus.GOING)

        assertEquals(1, repo.updateCalls.size)
        assertEquals(hangoutId, repo.updateCalls[0].first)
        assertEquals(userId, repo.updateCalls[0].second)
        assertEquals(AttendanceStatus.GOING, repo.updateCalls[0].third)
        assertEquals(0, repo.removeCalls.size)
    }

    @Test
    fun invoke_withNullStatus_callsRemoveAttendee() = runTest {
        val repo = FakeHangoutRepository()
        val useCase = UpdateAttendanceUseCase(repo)
        val hangoutId = UUID.randomUUID()
        val userId = UUID.randomUUID()

        useCase(hangoutId, userId, null)

        assertEquals(0, repo.updateCalls.size)
        assertEquals(1, repo.removeCalls.size)
        assertEquals(hangoutId, repo.removeCalls[0].first)
        assertEquals(userId, repo.removeCalls[0].second)
    }

    @Test
    fun invoke_withPresentStatus_callsUpdateWithPresent() = runTest {
        val repo = FakeHangoutRepository()
        val useCase = UpdateAttendanceUseCase(repo)

        useCase(UUID.randomUUID(), UUID.randomUUID(), AttendanceStatus.PRESENT)

        assertEquals(AttendanceStatus.PRESENT, repo.updateCalls.first().third)
    }
}
