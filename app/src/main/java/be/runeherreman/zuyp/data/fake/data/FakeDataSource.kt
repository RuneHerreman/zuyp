package be.runeherreman.zuyp.data.fake.data

import be.runeherreman.zuyp.data.fake.dto.HangoutDto
import be.runeherreman.zuyp.domain.model.AttendanceStatus
import be.runeherreman.zuyp.domain.model.User
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeDataSource @Inject constructor() {
    private fun goingAttendees(vararg users: User): List<User> =
        users.map { it.copy(attendanceStatus = AttendanceStatus.GOING) }

    private val hangoutLiveTest = HangoutDto(
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
    )

    private val hangoutKroegentoch = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000002"),
        "Bruges Bar Crawl",
        "",
        "The Monk Brugge",
        51.2105,
        3.2223,
        LocalDateTime.now().plusDays(2).withHour(19).withMinute(0).withSecond(0).withNano(0),
        LocalDateTime.now().plusDays(2).withHour(23).withMinute(0).withSecond(0).withNano(0),
        goingAttendees(FakeUsers.userBram, FakeUsers.userElise, FakeUsers.userTibo),
        FakeUsers.userBram,
        false
    )
    private val hangoutTropical = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000003"),
        "Tropical TD @ VUB",
        "",
        "VUB BSG",
        50.8224,
        4.3948,
        LocalDateTime.now().plusDays(5).withHour(20).withMinute(0).withSecond(0).withNano(0),
        LocalDateTime.now().plusDays(6).withHour(4).withMinute(0).withSecond(0).withNano(0),
        emptyList(),
        FakeUsers.userMila,
        true
    )
    private val hangoutPatersgat = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000004"),
        "Student Night at Patersgat",
        "",
        "Patersgat",
        51.2093,
        3.2247,
        LocalDateTime.now().plusDays(7).withHour(19).withMinute(30).withSecond(0).withNano(0),
        LocalDateTime.now().plusDays(8).withHour(1).withMinute(30).withSecond(0).withNano(0),
        emptyList(),
        FakeUsers.userMila,
        true
    )
    private val hangoutMovieNight = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000005"),
        "Cinema: One Battle After Another",
        "",
        "Cinema Lumiere Brugge",
        51.2114,
        3.2272,
        LocalDateTime.now().plusDays(9).withHour(19).withMinute(30).withSecond(0).withNano(0),
        LocalDateTime.now().plusDays(9).withHour(22).withMinute(30).withSecond(0).withNano(0),
        goingAttendees(FakeUsers.userMila, FakeUsers.userRuben, FakeUsers.userLotte, FakeUsers.userNoor),
        FakeUsers.userMila,
        true
    )
    private val hangoutFuseCrew = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000006"),
        "End of year BBQ",
        "",
        "VUB Campus",
        50.8500,
        4.3632,
        LocalDateTime.now().plusDays(11).withHour(23).withMinute(0).withSecond(0).withNano(0),
        LocalDateTime.now().plusDays(12).withHour(5).withMinute(0).withSecond(0).withNano(0),
        goingAttendees(FakeUsers.userSanne, FakeUsers.userDaan, FakeUsers.userThijs, FakeUsers.userRyan),
        FakeUsers.userDaan,
        false
    )
    private val hangoutHuisfeestje = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000008"),
        "House Party in Leuven",
        "",
        "Studentenkamer Leuven",
        50.8798,
        4.7005,
        LocalDateTime.now().plusDays(16).withHour(20).withMinute(0).withSecond(0).withNano(0),
        LocalDateTime.now().plusDays(17).withHour(2).withMinute(0).withSecond(0).withNano(0),
        goingAttendees(FakeUsers.userJoren, FakeUsers.userIsabella, FakeUsers.userSebastian, FakeUsers.userNatasja),
        FakeUsers.userJoren,
        true
    )
    private val hangoutCafeUitstap = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000009"),
        "Drinks at Café Puur",
        "",
        "Café Puur Antwerpen",
        51.2194,
        4.4024,
        LocalDateTime.now().plusDays(18).withHour(19).withMinute(0).withSecond(0).withNano(0),
        LocalDateTime.now().plusDays(18).withHour(23).withMinute(0).withSecond(0).withNano(0),
        goingAttendees(FakeUsers.userThijs, FakeUsers.userStéphanie, FakeUsers.userMarkus),
        FakeUsers.userThijs,
        false
    )
    private val hangoutThuisfeestje = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000011"),
        "Loft House Party",
        "",
        "Loft Sint-Gillis",
        50.8548,
        4.3457,
        LocalDateTime.now().plusDays(22).withHour(21).withMinute(0).withSecond(0).withNano(0),
        LocalDateTime.now().plusDays(23).withHour(3).withMinute(0).withSecond(0).withNano(0),
        goingAttendees(FakeUsers.userSophie, FakeUsers.userQuentin, FakeUsers.userEva, FakeUsers.userLars),
        FakeUsers.userSophie,
        true
    )
    private val hangoutKroegenTour = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000012"),
        "Brewery Tour: Oud Beersel",
        "",
        "Brouwerij Oud Beersel",
        51.0356,
        3.7161,
        LocalDateTime.now().plusDays(25).withHour(20).withMinute(0).withSecond(0).withNano(0),
        LocalDateTime.now().plusDays(26).withHour(2).withMinute(0).withSecond(0).withNano(0),
        goingAttendees(FakeUsers.userAnna, FakeUsers.userTom, FakeUsers.userEmilie),
        FakeUsers.userTom,
        false
    )
    private val hangoutFestivalCircuit = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000013"),
        "Concert at Sportpaleis",
        "",
        "Sportpaleis Antwerpen",
        51.2183,
        4.4141,
        LocalDateTime.now().plusDays(28).withHour(14).withMinute(0).withSecond(0).withNano(0),
        LocalDateTime.now().plusDays(28).withHour(22).withMinute(0).withSecond(0).withNano(0),
        goingAttendees(FakeUsers.userPhilip, FakeUsers.userClaire, FakeUsers.userSven, FakeUsers.userBeat, FakeUsers.userLena),
        FakeUsers.userPhilip,
        false
    )
    private val hangoutFuseRaveCrew = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000014"),
        "Fuse After Dark",
        "",
        "Fuse Bruxelles",
        50.8500,
        4.3632,
        LocalDateTime.now().plusDays(31).withHour(23).withMinute(0).withSecond(0).withNano(0),
        LocalDateTime.now().plusDays(32).withHour(5).withMinute(0).withSecond(0).withNano(0),
        goingAttendees(FakeUsers.userJulian, FakeUsers.userSienna, FakeUsers.userAlex),
        FakeUsers.userJulian,
        false
    )
    private val hangoutStudentHuisfeest = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000015"),
        "Campus House Party",
        "",
        "KU Leuven Campus",
        50.8798,
        4.7005,
        LocalDateTime.now().plusDays(34).withHour(19).withMinute(0).withSecond(0).withNano(0),
        LocalDateTime.now().plusDays(35).withHour(1).withMinute(0).withSecond(0).withNano(0),
        goingAttendees(FakeUsers.userOliver, FakeUsers.userMaya, FakeUsers.userLuc, FakeUsers.userAnne),
        FakeUsers.userOliver,
        true
    )
    private val hangoutCafeNightOut = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000016"),
        "Night Out at Bar Bram",
        "",
        "Bar Bram Bruxelles",
        50.8487,
        4.3572,
        LocalDateTime.of(2026, 4, 20, 19, 30),
        LocalDateTime.of(2026, 4, 21, 1, 30),
        goingAttendees(FakeUsers.userDavid, FakeUsers.userFlorence),
        FakeUsers.userDavid,
        false
    )
    private val hangoutPredrinks = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000018"),
        "Pre-Drinks in Antwerp",
        "",
        "Studentenhuis Antwerpen",
        51.2194,
        4.4024,
        LocalDateTime.of(2026, 4, 18, 21, 0),
        LocalDateTime.of(2026, 4, 19, 2, 0),
        goingAttendees(FakeUsers.userSteve, FakeUsers.userJessica, FakeUsers.userPaul, FakeUsers.userRosa),
        FakeUsers.userSteve,
        true
    )
    private val hangoutWhiskeyTasting = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000020"),
        "Whiskey Tasting Night",
        "",
        "Whiskey Café Gent",
        51.0519,
        3.7176,
        LocalDateTime.of(2026, 4, 19, 19, 0),
        LocalDateTime.of(2026, 4, 19, 22, 0),
        goingAttendees(FakeUsers.userFelix, FakeUsers.userZoe, FakeUsers.userJoren),
        FakeUsers.userFelix,
        false
    )
    private val hangoutTuinfeestje = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000021"),
        "Garden Party at VUB",
        "",
        "VUB Campus Brussel",
        50.8224,
        4.3948,
        LocalDateTime.of(2026, 4, 24, 18, 0),
        LocalDateTime.of(2026, 4, 25, 2, 0),
        goingAttendees(FakeUsers.userIsabella, FakeUsers.userSebastian, FakeUsers.userNatasja, FakeUsers.userThijs),
        FakeUsers.userIsabella,
        true
    )
    private val hangoutPintPizza = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000022"),
        "Pint & Pizza Night",
        "",
        "Pizzeria Napoli Leuven",
        50.8798,
        4.7005,
        LocalDateTime.of(2026, 4, 21, 19, 30),
        LocalDateTime.of(2026, 4, 22, 1, 30),
        goingAttendees(FakeUsers.userStéphanie, FakeUsers.userMarkus, FakeUsers.userCamille, FakeUsers.userVictoria),
        FakeUsers.userMarkus,
        false
    )
    private val hangoutRaveVoorbereiding = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000023"),
        "Pre-Rave Warm-up",
        "",
        "Flat Brussel",
        50.8500,
        4.3600,
        LocalDateTime.of(2026, 4, 23, 20, 0),
        LocalDateTime.of(2026, 4, 24, 2, 0),
        goingAttendees(FakeUsers.userDieter, FakeUsers.userLea, FakeUsers.userRyan, FakeUsers.userSophie),
        FakeUsers.userRyan,
        true
    )
    private val hangoutBeachParty = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000024"),
        "Beach Party in Knokke",
        "",
        "Strand Knokke",
        51.3557,
        3.2796,
        LocalDateTime.of(2026, 5, 2, 15, 0),
        LocalDateTime.of(2026, 5, 2, 22, 0),
        goingAttendees(FakeUsers.userQuentin, FakeUsers.userEva, FakeUsers.userLars, FakeUsers.userAnna, FakeUsers.userTom),
        FakeUsers.userQuentin,
        false
    )
    private val hangoutHouseMusic = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000025"),
        "House Music Night",
        "",
        "Muziekclub Gent",
        51.0432,
        3.7299,
        LocalDateTime.of(2026, 4, 25, 23, 0),
        LocalDateTime.of(2026, 4, 26, 5, 0),
        goingAttendees(FakeUsers.userEmilie, FakeUsers.userPhilip, FakeUsers.userClaire),
        FakeUsers.userEmilie,
        false
    )
    private val hangoutStudentenkamer = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000026"),
        "Dorm Room Party",
        "",
        "Thuis Leuven",
        50.8798,
        4.7005,
        LocalDateTime.of(2026, 4, 28, 20, 0),
        LocalDateTime.of(2026, 4, 29, 2, 0),
        goingAttendees(FakeUsers.userSven, FakeUsers.userBeat, FakeUsers.userLena),
        FakeUsers.userBeat,
        true
    )
    private val hangoutCraftBeer = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000027"),
        "Craft Beer Tour",
        "",
        "Brouwerijen Route Brugge",
        51.2105,
        3.2223,
        LocalDateTime.of(2026, 4, 22, 18, 0),
        LocalDateTime.of(2026, 4, 23, 0, 0),
        goingAttendees(FakeUsers.userJulian, FakeUsers.userSienna, FakeUsers.userAlex, FakeUsers.userOliver),
        FakeUsers.userJulian,
        false
    )
    private val hangoutSpringFestival = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000028"),
        "Spring Festival in the Park",
        "",
        "Citadelpark Antwerpen",
        51.2135,
        4.4005,
        LocalDateTime.of(2026, 5, 3, 14, 0),
        LocalDateTime.of(2026, 5, 3, 22, 0),
        goingAttendees(FakeUsers.userMaya, FakeUsers.userLuc, FakeUsers.userAnne, FakeUsers.userDavid),
        FakeUsers.userMaya,
        false
    )
    private val hangoutClubNight = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000029"),
        "Club Night at Fuse",
        "",
        "Fuse Bruxelles",
        50.8500,
        4.3632,
        LocalDateTime.of(2026, 4, 24, 23, 30),
        LocalDateTime.of(2026, 4, 25, 5, 30),
        goingAttendees(FakeUsers.userFlorence, FakeUsers.userNico, FakeUsers.userGrace),
        FakeUsers.userFlorence,
        false
    )
    private val hangoutVibesAtHome = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000030"),
        "Cozy Night In",
        "",
        "Huisje Gent",
        51.0432,
        3.7299,
        LocalDateTime.of(2026, 4, 29, 21, 0),
        LocalDateTime.of(2026, 4, 30, 3, 0),
        goingAttendees(FakeUsers.userChris, FakeUsers.userMaria, FakeUsers.userSteve, FakeUsers.userJessica),
        FakeUsers.userChris,
        true
    )
    private val hangoutSpontaneHang = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000032"),
        "Campus Terrace Hangout",
        "",
        "Terras UGent Gent",
        51.0088,
        3.7153,
        LocalDateTime.of(2026, 4, 20, 16, 0),
        LocalDateTime.of(2026, 4, 20, 22, 0),
        goingAttendees(FakeUsers.userDaan, FakeUsers.userLuna, FakeUsers.userMaxim, FakeUsers.userAva, FakeUsers.userFelix, FakeUsers.userZoe),
        FakeUsers.userZoe,
        false
    )
    private val hangoutBeachclub = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000034"),
        "Summer Beach Club",
        "",
        "Beachclub Ostende",
        51.2247,
        2.9267,
        LocalDateTime.of(2026, 5, 4, 16, 0),
        LocalDateTime.of(2026, 5, 5, 0, 0),
        goingAttendees(FakeUsers.userNatasja, FakeUsers.userThijs, FakeUsers.userStéphanie, FakeUsers.userMarkus, FakeUsers.userCamille),
        FakeUsers.userNatasja,
        false
    )
    private val hangoutAfterparty = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000035"),
        "Loft Afterparty",
        "",
        "Loft Brussel",
        50.8548,
        4.3457,
        LocalDateTime.of(2026, 4, 25, 3, 0),
        LocalDateTime.of(2026, 4, 25, 8, 0),
        goingAttendees(FakeUsers.userVictoria, FakeUsers.userDieter, FakeUsers.userLea),
        FakeUsers.userVictoria,
        true
    )
    private val hangoutSummerKickoff = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000036"),
        "Summer Kick-off Party",
        "",
        "Tuin VUB",
        50.8224,
        4.3948,
        LocalDateTime.of(2026, 5, 10, 14, 0),
        LocalDateTime.of(2026, 5, 10, 22, 0),
        goingAttendees(FakeUsers.userRyan, FakeUsers.userSophie, FakeUsers.userQuentin, FakeUsers.userEva, FakeUsers.userLars),
        FakeUsers.userEva,
        false
    )
    private val hangoutWineDine = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000037"),
        "Wine & Dine at Bellini",
        "",
        "Restaurant Bellini Antwerpen",
        51.2194,
        4.4024,
        LocalDateTime.of(2026, 4, 26, 19, 0),
        LocalDateTime.of(2026, 4, 27, 1, 0),
        goingAttendees(FakeUsers.userAnna, FakeUsers.userTom, FakeUsers.userEmilie, FakeUsers.userPhilip),
        FakeUsers.userAnna,
        false
    )
    private val hangoutRaveAfterparty = HangoutDto(
        UUID.fromString("10000000-0000-0000-0000-000000000038"),
        "Rave Afterparty",
        "",
        "Woning Leuven",
        50.8798,
        4.7005,
        LocalDateTime.of(2026, 4, 27, 5, 0),
        LocalDateTime.of(2026, 4, 27, 10, 0),
        goingAttendees(FakeUsers.userClaire, FakeUsers.userSven, FakeUsers.userBeat),
        FakeUsers.userClaire,
        true
    )

    fun getHangouts(): List<HangoutDto> {
        return listOf(
            hangoutKroegentoch,
            hangoutTropical,
            hangoutPatersgat,
            hangoutMovieNight,
            hangoutFuseCrew,
            hangoutHuisfeestje,
            hangoutCafeUitstap,
            hangoutThuisfeestje,
            hangoutKroegenTour,
            hangoutFestivalCircuit,
            hangoutFuseRaveCrew,
            hangoutStudentHuisfeest,
            hangoutCafeNightOut,
            hangoutPredrinks,
            hangoutWhiskeyTasting,
            hangoutTuinfeestje,
            hangoutPintPizza,
            hangoutRaveVoorbereiding,
            hangoutBeachParty,
            hangoutHouseMusic,
            hangoutStudentenkamer,
            hangoutCraftBeer,
            hangoutSpringFestival,
            hangoutClubNight,
            hangoutVibesAtHome,
            hangoutSpontaneHang,
            hangoutBeachclub,
            hangoutAfterparty,
            hangoutSummerKickoff,
            hangoutWineDine,
            hangoutRaveAfterparty,
            hangoutLiveTest
        )
    }

    fun getHangoutById(id: UUID): HangoutDto? {
        return getHangouts().firstOrNull { id == it.id }
    }
}
