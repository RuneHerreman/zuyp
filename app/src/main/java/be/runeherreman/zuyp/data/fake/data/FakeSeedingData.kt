package be.runeherreman.zuyp.data.fake.data

import be.runeherreman.zuyp.data.fake.dto.HangoutDto
import be.runeherreman.zuyp.domain.model.AttendanceStatus
import java.time.LocalDateTime
import java.util.UUID

private fun inDays(days: Long, hourOfDay: Int = 20) =
    LocalDateTime.now().plusDays(days).withHour(hourOfDay).withMinute(0).withSecond(0).withNano(0)

private fun inDaysEnd(days: Long, hourOfDay: Int = 22) =
    LocalDateTime.now().plusDays(days).withHour(hourOfDay).withMinute(0).withSecond(0).withNano(0)

object FakeSeedingData {
    fun getHangouts(): List<HangoutDto> {
        return listOf(
            HangoutDto(
                UUID.fromString("00000000-0000-0000-0000-000000000099"),
                "Campus Pre-Drinks @ Howest",
                "",
                "Howest Brugge",
                51.2082,
                3.2241,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                listOf(FakeUsers.userKoen.copy(attendanceStatus = AttendanceStatus.GOING)),
                FakeUsers.userKoen,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Bruges Bar Crawl",
                "",
                "The Monk Brugge",
                51.2105,
                3.2223,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                listOf(FakeUsers.userBram, FakeUsers.userElise, FakeUsers.userTibo),
                FakeUsers.userBram,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Tropical TD @ VUB",
                "",
                "VUB BSG",
                50.8224,
                4.3948,
                inDays(2, 20), inDaysEnd(3, 4),
                emptyList(),
                FakeUsers.userMila,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Student Night at Patersgat",
                "",
                "Patersgat",
                51.2093,
                3.2247,
                inDays(4, 19), inDaysEnd(5, 1),
                emptyList(),
                FakeUsers.userMila,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Movie Night at Cinéma Lumière",
                "",
                "Cinema Lumiere Brugge",
                51.2114,
                3.2272,
                inDays(5, 19), inDaysEnd(5, 22),
                listOf(FakeUsers.userMila, FakeUsers.userRuben, FakeUsers.userLotte, FakeUsers.userNoor),
                FakeUsers.userMila,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Fuse Saturday Night",
                "",
                "Fuse Bruxelles",
                50.8500,
                4.3632,
                inDays(1, 23), inDaysEnd(2, 5),
                listOf(FakeUsers.userSanne, FakeUsers.userDaan, FakeUsers.userThijs, FakeUsers.userRyan),
                FakeUsers.userDaan,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "House Party in Leuven",
                "",
                "Studentenkamer Leuven",
                50.8798,
                4.7005,
                inDays(6, 20), inDaysEnd(7, 2),
                listOf(FakeUsers.userJoren, FakeUsers.userIsabella, FakeUsers.userSebastian, FakeUsers.userNatasja),
                FakeUsers.userJoren,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Drinks at Café Puur",
                "",
                "Café Puur Antwerpen",
                51.2194,
                4.4024,
                inDays(7, 19), inDaysEnd(7, 23),
                listOf(FakeUsers.userThijs, FakeUsers.userStéphanie, FakeUsers.userMarkus),
                FakeUsers.userThijs,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Loft House Party",
                "",
                "Loft Sint-Gillis",
                50.8548,
                4.3457,
                inDays(2, 21), inDaysEnd(3, 3),
                listOf(FakeUsers.userSophie, FakeUsers.userQuentin, FakeUsers.userEva, FakeUsers.userLars),
                FakeUsers.userSophie,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Brewery Tour: Oud Beersel",
                "",
                "Brouwerij Oud Beersel",
                51.0356,
                3.7161,
                inDays(4, 20), inDaysEnd(5, 2),
                listOf(FakeUsers.userAnna, FakeUsers.userTom, FakeUsers.userEmilie),
                FakeUsers.userTom,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Concert at Sportpaleis",
                "",
                "Sportpaleis Antwerpen",
                51.2183,
                4.4141,
                inDays(10, 14), inDaysEnd(10, 22),
                listOf(FakeUsers.userPhilip, FakeUsers.userClaire, FakeUsers.userSven, FakeUsers.userBeat, FakeUsers.userLena),
                FakeUsers.userPhilip,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Fuse After Dark",
                "",
                "Fuse Bruxelles",
                50.8500,
                4.3632,
                inDays(6, 23), inDaysEnd(7, 5),
                listOf(FakeUsers.userJulian, FakeUsers.userSienna, FakeUsers.userAlex),
                FakeUsers.userJulian,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Campus House Party",
                "",
                "KU Leuven Campus",
                50.8798,
                4.7005,
                inDays(5, 19), inDaysEnd(6, 1),
                listOf(FakeUsers.userOliver, FakeUsers.userMaya, FakeUsers.userLuc, FakeUsers.userAnne),
                FakeUsers.userOliver,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Night Out at Bar Bram",
                "",
                "Bar Bram Bruxelles",
                50.8487,
                4.3572,
                inDays(3, 19), inDaysEnd(4, 1),
                listOf(FakeUsers.userDavid, FakeUsers.userFlorence),
                FakeUsers.userDavid,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Pre-Drinks in Antwerp",
                "",
                "Studentenhuis Antwerpen",
                51.2194,
                4.4024,
                inDays(1, 21), inDaysEnd(2, 2),
                listOf(FakeUsers.userSteve, FakeUsers.userJessica, FakeUsers.userPaul, FakeUsers.userRosa),
                FakeUsers.userSteve,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Whiskey Tasting Night",
                "",
                "Whiskey Café Gent",
                51.0519,
                3.7176,
                inDays(2, 19), inDaysEnd(2, 22),
                listOf(FakeUsers.userFelix, FakeUsers.userZoe, FakeUsers.userJoren),
                FakeUsers.userFelix,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Garden Party at VUB",
                "",
                "VUB Campus Brussel",
                50.8224,
                4.3948,
                inDays(7, 18), inDaysEnd(8, 2),
                listOf(FakeUsers.userIsabella, FakeUsers.userSebastian, FakeUsers.userNatasja, FakeUsers.userThijs),
                FakeUsers.userIsabella,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Pint & Pizza Night",
                "",
                "Pizzeria Napoli Leuven",
                50.8798,
                4.7005,
                inDays(4, 19), inDaysEnd(5, 1),
                listOf(FakeUsers.userStéphanie, FakeUsers.userMarkus, FakeUsers.userCamille, FakeUsers.userVictoria),
                FakeUsers.userMarkus,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Pre-Rave Warm-up",
                "",
                "Flat Brussel",
                50.8500,
                4.3600,
                inDays(6, 20), inDaysEnd(7, 2),
                listOf(FakeUsers.userDieter, FakeUsers.userLea, FakeUsers.userRyan, FakeUsers.userSophie),
                FakeUsers.userRyan,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Beach Party in Knokke",
                "",
                "Strand Knokke",
                51.3557,
                3.2796,
                inDays(12, 15), inDaysEnd(12, 22),
                listOf(FakeUsers.userQuentin, FakeUsers.userEva, FakeUsers.userLars, FakeUsers.userAnna, FakeUsers.userTom),
                FakeUsers.userQuentin,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "House Music Night",
                "",
                "Muziekclub Gent",
                51.0432,
                3.7299,
                inDays(8, 23), inDaysEnd(9, 5),
                listOf(FakeUsers.userEmilie, FakeUsers.userPhilip, FakeUsers.userClaire),
                FakeUsers.userEmilie,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Dorm Room Party",
                "",
                "Thuis Leuven",
                50.8798,
                4.7005,
                inDays(11, 20), inDaysEnd(12, 2),
                listOf(FakeUsers.userSven, FakeUsers.userBeat, FakeUsers.userLena),
                FakeUsers.userBeat,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Craft Beer Tour",
                "",
                "Brouwerijen Route Brugge",
                51.2105,
                3.2223,
                inDays(5, 18), inDaysEnd(6, 0),
                listOf(FakeUsers.userJulian, FakeUsers.userSienna, FakeUsers.userAlex, FakeUsers.userOliver),
                FakeUsers.userJulian,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Spring Festival in the Park",
                "",
                "Citadelpark Antwerpen",
                51.2135,
                4.4005,
                inDays(13, 14), inDaysEnd(13, 22),
                listOf(FakeUsers.userMaya, FakeUsers.userLuc, FakeUsers.userAnne, FakeUsers.userDavid),
                FakeUsers.userMaya,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Club Night at Fuse",
                "",
                "Fuse Bruxelles",
                50.8500,
                4.3632,
                inDays(7, 23), inDaysEnd(8, 5),
                listOf(FakeUsers.userFlorence, FakeUsers.userNico, FakeUsers.userGrace),
                FakeUsers.userFlorence,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Cozy Night In",
                "",
                "Huisje Gent",
                51.0432,
                3.7299,
                inDays(12, 21), inDaysEnd(13, 3),
                listOf(FakeUsers.userChris, FakeUsers.userMaria, FakeUsers.userSteve, FakeUsers.userJessica),
                FakeUsers.userChris,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Campus Terrace Hangout",
                "",
                "Terras UGent Gent",
                51.0088,
                3.7153,
                inDays(3, 16), inDaysEnd(3, 22),
                listOf(FakeUsers.userDaan, FakeUsers.userLuna, FakeUsers.userMaxim, FakeUsers.userAva, FakeUsers.userFelix, FakeUsers.userZoe),
                FakeUsers.userZoe,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Summer Beach Club",
                "",
                "Beachclub Ostende",
                51.2247,
                2.9267,
                inDays(14, 16), inDaysEnd(15, 0),
                listOf(FakeUsers.userNatasja, FakeUsers.userThijs, FakeUsers.userStéphanie, FakeUsers.userMarkus, FakeUsers.userCamille),
                FakeUsers.userNatasja,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Loft Afterparty",
                "",
                "Loft Brussel",
                50.8548,
                4.3457,
                inDays(8, 3), inDaysEnd(8, 8),
                listOf(FakeUsers.userVictoria, FakeUsers.userDieter, FakeUsers.userLea),
                FakeUsers.userVictoria,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Summer Kick-off Party",
                "",
                "Tuin VUB",
                50.8224,
                4.3948,
                inDays(20, 14), inDaysEnd(20, 22),
                listOf(FakeUsers.userRyan, FakeUsers.userSophie, FakeUsers.userQuentin, FakeUsers.userEva, FakeUsers.userLars),
                FakeUsers.userEva,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Wine & Dine at Bellini",
                "",
                "Restaurant Bellini Antwerpen",
                51.2194,
                4.4024,
                inDays(9, 19), inDaysEnd(10, 1),
                listOf(FakeUsers.userAnna, FakeUsers.userTom, FakeUsers.userEmilie, FakeUsers.userPhilip),
                FakeUsers.userAnna,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Rave Afterparty",
                "",
                "Woning Leuven",
                50.8798,
                4.7005,
                inDays(10, 5), inDaysEnd(10, 10),
                listOf(FakeUsers.userClaire, FakeUsers.userSven, FakeUsers.userBeat),
                FakeUsers.userClaire,
                true
            )
        )
    }
}
