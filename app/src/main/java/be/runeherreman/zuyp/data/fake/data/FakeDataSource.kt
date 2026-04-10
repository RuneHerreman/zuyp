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
    fun getHangouts(): List<HangoutDto>{
        return listOf<HangoutDto>(
            HangoutDto(
                UUID.randomUUID(),
                "Onlynumbers, Vladimir Couchemar",
                "",
                "Kompass Klub Gent",
                LocalDateTime.now(),
                listOf(
                    User(
                        UUID.randomUUID(),
                        "Jan Ketelman",
                        LocalDate.of(2006, 8, 20),
                        "jan.ketelman@gmail.com"
                    ),
                    User(
                        UUID.randomUUID(),
                        "Koen Koreman",
                        LocalDate.of(2002, 7, 20),
                        "koen.koreman@gmail.com"
                    ),
                    User(
                        UUID.randomUUID(),
                        "Lotte Van Damme",
                        LocalDate.of(2004, 3, 14),
                        "lotte.vandamme@gmail.com"
                    ),
                    User(
                        UUID.randomUUID(),
                        "Milan De Smet",
                        LocalDate.of(2001, 11, 5),
                        "milan.desmet@gmail.com"
                    ),
                    User(
                        UUID.randomUUID(),
                        "Nora Peeters",
                        LocalDate.of(2005, 1, 27),
                        "nora.peeters@gmail.com"
                    )
                ),
                User(
                    UUID.randomUUID(),
                    "Jan Ketelman",
                    LocalDate.of(2006, 8, 20),
                    "jan.ketelman@gmail.com"
                ),
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Kroegentoch Brugge",
                "",
                "The Monk Brugge",
                LocalDateTime.now(),
                listOf(
                    User(
                        UUID.randomUUID(),
                        "Bram Verdonck",
                        LocalDate.of(2003, 6, 9),
                        "bram.verdonck@gmail.com"
                    ),
                    User(
                        UUID.randomUUID(),
                        "Elise Maes",
                        LocalDate.of(2004, 12, 1),
                        "elise.maes@gmail.com"
                    ),
                    User(
                        UUID.randomUUID(),
                        "Tibo Claes",
                        LocalDate.of(2002, 4, 18),
                        "tibo.claes@gmail.com"
                    )
                ),
                User(
                    UUID.randomUUID(),
                    "Bram Verdonck",
                    LocalDate.of(2003, 6, 9),
                    "bram.verdonck@gmail.com"
                ),
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Tropical TD",
                "",
                "VUB BSG",
                LocalDateTime.of(2026, 3, 24, 20, 0),
                emptyList(),
                User(
                    UUID.randomUUID(),
                    "Mila Janssens",
                    LocalDate.of(2004, 2, 11),
                    "mila.janssens@gmail.com"
                ),
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Patersgat Silme",
                "",
                "Patersgat",
                LocalDateTime.of(2026, 4, 9, 19, 30),
                emptyList(),
                User(
                    UUID.randomUUID(),
                    "Mila Janssens",
                    LocalDate.of(2004, 2, 11),
                    "mila.janssens@gmail.com"
                ),
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "No other choice - Movie Night",
                "",
                "Cinema Lumiere Brugge",
                LocalDateTime.of(2026, 4, 19, 19, 30),
                listOf(
                    User(
                        UUID.randomUUID(),
                        "Mila Janssens",
                        LocalDate.of(2004, 2, 11),
                        "mila.janssens@gmail.com"
                    ),
                    User(
                        UUID.randomUUID(),
                        "Ruben Claeys",
                        LocalDate.of(2002, 5, 7),
                        "ruben.claeys@gmail.com"
                    ),
                    User(
                        UUID.randomUUID(),
                        "Lotte Van Damme",
                        LocalDate.of(2004, 3, 14),
                        "lotte.vandamme@gmail.com"
                    ),
                    User(
                        UUID.randomUUID(),
                        "Noor De Wilde",
                        LocalDate.of(2005, 10, 22),
                        "noor.dewilde@gmail.com"
                    )
                ),
                User(
                    UUID.randomUUID(),
                    "Mila Janssens",
                    LocalDate.of(2004, 2, 11),
                    "mila.janssens@gmail.com"
                ),
                true
            )
        )
    }

    fun getHangoutById(id: UUID): HangoutDto {
        return getHangouts().first { id == it.id }
    }
}