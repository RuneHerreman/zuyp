package be.runeherreman.zuyp.data.repositories.room

import be.runeherreman.zuyp.data.local.room.entity.expenses.ExpenseEntity
import be.runeherreman.zuyp.data.local.room.entity.expenses.ExpenseWithDetails
import be.runeherreman.zuyp.data.local.room.entity.groups.GroupEntity
import be.runeherreman.zuyp.data.local.room.entity.groups.GroupWithMembers
import be.runeherreman.zuyp.data.local.room.entity.hangouts.AttendanceStatus
import be.runeherreman.zuyp.data.local.room.entity.hangouts.HangoutEntity
import be.runeherreman.zuyp.data.local.room.entity.hangouts.HangoutWithDetails
import be.runeherreman.zuyp.data.local.room.entity.users.UserEntity
import be.runeherreman.zuyp.domain.model.Expense
import be.runeherreman.zuyp.domain.model.ExpenseShare
import be.runeherreman.zuyp.domain.model.Group
import be.runeherreman.zuyp.domain.model.Hangout
import be.runeherreman.zuyp.domain.model.User


fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        birthdate = birthdate,
        email = email,
        imageUrl = imageUrl
    )
}
fun Group.toEntity(): GroupEntity {
    return GroupEntity(
        id = id,
        name = name,
        creatorId = creatorId,
        description = description,
        imageUrl = ""
    )
}
fun Hangout.toEntity(): HangoutEntity {
    return HangoutEntity(
        id = id,
        title = title,
        description = description,
        locationName = locationName,
        latitude = latitude,
        longitude = longitude,
        startDate = startDate,
        endDate = endDate,
        creatorId = creator.id,
        private = private
    )
}
fun Expense.toEntity(): ExpenseEntity = ExpenseEntity(
    id = id,
    hangoutId = hangoutId,
    paidByUserId = paidBy.id,
    title = title,
    amount = amount,
    imageUri = imageUri,
    createdAt = createdAt
)


fun UserEntity.toDomain(): User {
    return User(
        id = id,
        name = name,
        birthdate = birthdate,
        email = email,
        imageUrl = imageUrl
    )
}
fun HangoutWithDetails.toDomain(): Hangout {
    val statusMap = attendanceStatuses.associateBy { it.userId }
    return Hangout(
        id = hangout.id,
        title = hangout.title,
        description = hangout.description,
        locationName = hangout.locationName,
        latitude = hangout.latitude,
        longitude = hangout.longitude,
        startDate = hangout.startDate,
        endDate = hangout.endDate,
        attendees = attendees.map { userEntity ->
            userEntity.toDomain(statusMap[userEntity.id]?.status)
        },
        creator = creator.toDomain(),
        private = hangout.private
    )
}
fun UserEntity.toDomain(attendanceStatus: AttendanceStatus? = null): User {
    return User(
        id = id,
        name = name,
        birthdate = birthdate,
        email = email,
        imageUrl = imageUrl,
        attendanceStatus = attendanceStatus?.let {
            when (it) {
                AttendanceStatus.GOING -> AttendanceStatus.GOING
                AttendanceStatus.NOT_INTERESTED -> AttendanceStatus.NOT_INTERESTED
            }
        }
    )
}
fun GroupWithMembers.toDomain(): Group {
    return Group(
        id = group.id,
        name = group.name,
        creatorId = group.creatorId,
        description = group.description,
        members = members.map { it.toDomain() }
    )
}
fun ExpenseWithDetails.toDomain(): Expense {
    val userById = participants.associateBy { it.id }

    val shares = shares.mapNotNull { share ->
        userById[share.userId]?.let { user ->
            ExpenseShare(user = user.toDomain(), amount = share.shareAmount)
        }
    }

    return Expense(
        id = expense.id,
        hangoutId = expense.hangoutId,
        title = expense.title,
        amount = expense.amount,
        paidBy = payer.toDomain(),
        imageUri = expense.imageUri,
        createdAt = expense.createdAt,
        shares = shares
    )
}
