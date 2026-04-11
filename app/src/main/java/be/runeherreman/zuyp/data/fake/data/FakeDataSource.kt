package be.runeherreman.zuyp.data.fake.data

import be.runeherreman.zuyp.data.fake.dto.HangoutDto
import be.runeherreman.zuyp.domain.model.User
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeDataSource @Inject constructor() {
    private val userJan = User(
        UUID.fromString("d68d1840-0610-449e-8c34-8c8872087c5e"),
        "Jan Ketelman",
        LocalDate.of(2006, 8, 20),
        "jan.ketelman@gmail.com"
    )
    private val userKoen = User(
        UUID.fromString("6a02b6e8-3195-46a2-9721-3f8909405f6d"),
        "Koen Koreman",
        LocalDate.of(2002, 7, 20),
        "koen.koreman@gmail.com"
    )
    private val userLotte = User(
        UUID.fromString("2f7c006b-76f5-419a-9e2e-2a440632d43e"),
        "Lotte Van Damme",
        LocalDate.of(2004, 3, 14),
        "lotte.vandamme@gmail.com"
    )
    private val userMilan = User(
        UUID.fromString("69389e72-2d93-4e4c-8367-15e7178b056e"),
        "Milan De Smet",
        LocalDate.of(2001, 11, 5),
        "milan.desmet@gmail.com"
    )
    private val userNora = User(
        UUID.fromString("f4b1d6e8-3195-46a2-9721-3f8909405f6f"),
        "Nora Peeters",
        LocalDate.of(2005, 1, 27),
        "nora.peeters@gmail.com"
    )
    private val userBram = User(
        UUID.fromString("a123b456-7890-4def-9012-34567890abcd"),
        "Bram Verdonck",
        LocalDate.of(2003, 6, 9),
        "bram.verdonck@gmail.com"
    )
    private val userElise = User(
        UUID.fromString("b234c567-8901-4ef0-0123-45678901bcde"),
        "Elise Maes",
        LocalDate.of(2004, 12, 1),
        "elise.maes@gmail.com"
    )
    private val userTibo = User(
        UUID.fromString("c345d678-9012-4f01-1234-56789012cdef"),
        "Tibo Claes",
        LocalDate.of(2002, 4, 18),
        "tibo.claes@gmail.com"
    )
    private val userMila = User(
        UUID.fromString("d456e789-0123-4012-2345-67890123def0"),
        "Mila Janssens",
        LocalDate.of(2004, 2, 11),
        "mila.janssens@gmail.com"
    )
    private val userRuben = User(
        UUID.fromString("e567f890-1234-4123-3456-78901234ef01"),
        "Ruben Claeys",
        LocalDate.of(2002, 5, 7),
        "ruben.claeys@gmail.com"
    )
    private val userNoor = User(
        UUID.fromString("f678a901-2345-4234-4567-89012345f012"),
        "Noor De Wilde",
        LocalDate.of(2005, 10, 22),
        "noor.dewilde@gmail.com"
    )

    fun getHangouts(): List<HangoutDto> {
        return listOf(
            HangoutDto(
                UUID.fromString("a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d"),
                "Onlynumbers, Vladimir Couchemar",
                "",
                "Kompass Klub Gent",
                51.0378,
                3.7042,
                LocalDateTime.now(),
                listOf(userJan, userKoen, userLotte, userMilan, userNora),
                userJan,
                false
            ),
            HangoutDto(
                UUID.fromString("b2c3d4e5-f6a7-4b6c-9d0e-1f2a3b4c5d6e"),
                "Kroegentoch Brugge",
                "",
                "The Monk Brugge",
                51.2105,
                3.2223,
                LocalDateTime.now(),
                listOf(userBram, userElise, userTibo),
                userBram,
                false
            ),
            HangoutDto(
                UUID.fromString("c3d4e5f6-a7b8-4c7d-0e1f-2a3b4c5d6e7f"),
                "Tropical TD",
                "",
                "VUB BSG",
                50.8224,
                4.3948,
                LocalDateTime.of(2026, 3, 24, 20, 0),
                emptyList(),
                userMila,
                true
            ),
            HangoutDto(
                UUID.fromString("d4e5f6a7-b8c9-4d8e-1f2a-3b4c5d6e7f8a"),
                "Patersgat Silme",
                "",
                "Patersgat",
                51.2093,
                3.2247,
                LocalDateTime.of(2026, 4, 9, 19, 30),
                emptyList(),
                userMila,
                true
            ),
            HangoutDto(
                UUID.fromString("e5f6a7b8-c9d0-4e9f-2a3b-4c5d6e7f8a9b"),
                "No other choice - Movie Night",
                "",
                "Cinema Lumiere Brugge",
                51.2114,
                3.2272,
                LocalDateTime.of(2026, 4, 19, 19, 30),
                listOf(userMila, userRuben, userLotte, userNoor),
                userMila,
                true
            )
        )
    }

    fun getHangoutById(id: UUID): HangoutDto {
        return getHangouts().first { id == it.id }
    }
}
