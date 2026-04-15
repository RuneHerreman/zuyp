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
        UUID.randomUUID(),
        "Jan Ketelman",
        LocalDate.of(2006, 8, 20),
        "jan.ketelman@gmail.com"
    )
    private val userKoen = User(
        UUID.fromString("01234566-8f09-4567-4af8-def000000014"),
        "Koen Koreman",
        LocalDate.of(2002, 7, 20),
        "koen.koreman@gmail.com"
    )
    private val userLotte = User(
        UUID.randomUUID(),
        "Lotte Van Damme",
        LocalDate.of(2004, 3, 14),
        "lotte.vandamme@gmail.com"
    )
    private val userMilan = User(
        UUID.randomUUID(),
        "Milan De Smet",
        LocalDate.of(2001, 11, 5),
        "milan.desmet@gmail.com"
    )
    private val userNora = User(
        UUID.randomUUID(),
        "Nora Peeters",
        LocalDate.of(2005, 1, 27),
        "nora.peeters@gmail.com"
    )
    private val userBram = User(
        UUID.randomUUID(),
        "Bram Verdonck",
        LocalDate.of(2003, 6, 9),
        "bram.verdonck@gmail.com"
    )
    private val userElise = User(
        UUID.randomUUID(),
        "Elise Maes",
        LocalDate.of(2004, 12, 1),
        "elise.maes@gmail.com"
    )
    private val userTibo = User(
        UUID.randomUUID(),
        "Tibo Claes",
        LocalDate.of(2002, 4, 18),
        "tibo.claes@gmail.com"
    )
    private val userMila = User(
        UUID.randomUUID(),
        "Mila Janssens",
        LocalDate.of(2004, 2, 11),
        "mila.janssens@gmail.com"
    )
    private val userRuben = User(
        UUID.randomUUID(),
        "Ruben Claeys",
        LocalDate.of(2002, 5, 7),
        "ruben.claeys@gmail.com"
    )
    private val userNoor = User(
        UUID.randomUUID(),
        "Noor De Wilde",
        LocalDate.of(2005, 10, 22),
        "noor.dewilde@gmail.com"
    )
    private val userSanne = User(
        UUID.randomUUID(),
        "Sanne Desmet",
        LocalDate.of(2004, 7, 14),
        "sanne.desmet@gmail.com"
    )
    private val userDaan = User(
        UUID.randomUUID(),
        "Daan Heylen",
        LocalDate.of(2003, 9, 3),
        "daan.heylen@gmail.com"
    )
    private val userLuna = User(
        UUID.randomUUID(),
        "Luna Smets",
        LocalDate.of(2005, 5, 19),
        "luna.smets@gmail.com"
    )
    private val userMaxim = User(
        UUID.randomUUID(),
        "Maxim Vercammen",
        LocalDate.of(2002, 11, 11),
        "maxim.vercammen@gmail.com"
    )
    private val userAva = User(
        UUID.randomUUID(),
        "Ava Vermeersch",
        LocalDate.of(2004, 8, 26),
        "ava.vermeersch@gmail.com"
    )
    private val userFelix = User(
        UUID.randomUUID(),
        "Felix Vandebrouck",
        LocalDate.of(2003, 1, 8),
        "felix.vandebrouck@gmail.com"
    )
    private val userZoe = User(
        UUID.randomUUID(),
        "Zoe Vandersmissen",
        LocalDate.of(2005, 3, 22),
        "zoe.vandersmissen@gmail.com"
    )
    private val userJoren = User(
        UUID.randomUUID(),
        "Joren Verlinden",
        LocalDate.of(2002, 10, 5),
        "joren.verlinden@gmail.com"
    )
    private val userIsabella = User(
        UUID.randomUUID(),
        "Isabella Verhaeghe",
        LocalDate.of(2004, 6, 17),
        "isabella.verhaeghe@gmail.com"
    )
    private val userSebastian = User(
        UUID.randomUUID(),
        "Sebastian Lampe",
        LocalDate.of(2001, 12, 30),
        "sebastian.lampe@gmail.com"
    )
    private val userNatasja = User(
        UUID.randomUUID(),
        "Natasja Vos",
        LocalDate.of(2004, 4, 11),
        "natasja.vos@gmail.com"
    )
    private val userThijs = User(
        UUID.randomUUID(),
        "Thijs Daemen",
        LocalDate.of(2003, 7, 28),
        "thijs.daemen@gmail.com"
    )
    private val userStéphanie = User(
        UUID.randomUUID(),
        "Stéphanie Devos",
        LocalDate.of(2004, 2, 9),
        "stephanie.devos@gmail.com"
    )
    private val userMarkus = User(
        UUID.randomUUID(),
        "Markus Peeters",
        LocalDate.of(2002, 8, 14),
        "markus.peeters@gmail.com"
    )
    private val userCamille = User(
        UUID.randomUUID(),
        "Camille Ronsse",
        LocalDate.of(2005, 9, 2),
        "camille.ronsse@gmail.com"
    )
    private val userVictoria = User(
        UUID.randomUUID(),
        "Victoria Leemans",
        LocalDate.of(2003, 5, 25),
        "victoria.leemans@gmail.com"
    )
    private val userDieter = User(
        UUID.randomUUID(),
        "Dieter Wagemans",
        LocalDate.of(2002, 3, 16),
        "dieter.wagemans@gmail.com"
    )
    private val userLea = User(
        UUID.randomUUID(),
        "Lea Snoeck",
        LocalDate.of(2004, 11, 7),
        "lea.snoeck@gmail.com"
    )
    private val userRyan = User(
        UUID.randomUUID(),
        "Ryan Debrouwer",
        LocalDate.of(2003, 6, 20),
        "ryan.debrouwer@gmail.com"
    )
    private val userSophie = User(
        UUID.randomUUID(),
        "Sophie Delvaux",
        LocalDate.of(2005, 1, 12),
        "sophie.delvaux@gmail.com"
    )
    private val userQuentin = User(
        UUID.randomUUID(),
        "Quentin Claes",
        LocalDate.of(2002, 9, 4),
        "quentin.claes@gmail.com"
    )
    private val userEva = User(
        UUID.randomUUID(),
        "Eva Vanheule",
        LocalDate.of(2004, 10, 19),
        "eva.vanheule@gmail.com"
    )
    private val userLars = User(
        UUID.randomUUID(),
        "Lars Bosmans",
        LocalDate.of(2003, 2, 8),
        "lars.bosmans@gmail.com"
    )
    private val userAnna = User(
        UUID.randomUUID(),
        "Anna Vanzeir",
        LocalDate.of(2004, 7, 31),
        "anna.vanzeir@gmail.com"
    )
    private val userTom = User(
        UUID.randomUUID(),
        "Tom Baert",
        LocalDate.of(2002, 4, 22),
        "tom.baert@gmail.com"
    )
    private val userEmilie = User(
        UUID.randomUUID(),
        "Emilie Briers",
        LocalDate.of(2005, 8, 6),
        "emilie.briers@gmail.com"
    )
    private val userPhilip = User(
        UUID.randomUUID(),
        "Philip Everard",
        LocalDate.of(2002, 6, 13),
        "philip.everard@gmail.com"
    )
    private val userClaire = User(
        UUID.randomUUID(),
        "Claire Genon",
        LocalDate.of(2004, 9, 29),
        "claire.genon@gmail.com"
    )
    private val userSven = User(
        UUID.randomUUID(),
        "Sven Guelinckx",
        LocalDate.of(2001, 12, 11),
        "sven.guelinckx@gmail.com"
    )
    private val userBeat = User(
        UUID.randomUUID(),
        "Beat Greyling",
        LocalDate.of(2003, 8, 24),
        "beat.greyling@gmail.com"
    )
    private val userLena = User(
        UUID.randomUUID(),
        "Lena Goyvaerts",
        LocalDate.of(2005, 4, 5),
        "lena.goyvaerts@gmail.com"
    )
    private val userJulian = User(
        UUID.randomUUID(),
        "Julian Groote",
        LocalDate.of(2003, 11, 18),
        "julian.groote@gmail.com"
    )
    private val userSienna = User(
        UUID.randomUUID(),
        "Sienna Gregoor",
        LocalDate.of(2004, 3, 27),
        "sienna.gregoor@gmail.com"
    )
    private val userAlex = User(
        UUID.randomUUID(),
        "Alex Grill",
        LocalDate.of(2002, 10, 9),
        "alex.grill@gmail.com"
    )
    private val userOliver = User(
        UUID.randomUUID(),
        "Oliver Hanssens",
        LocalDate.of(2005, 2, 14),
        "oliver.hanssens@gmail.com"
    )
    private val userMaya = User(
        UUID.randomUUID(),
        "Maya Vandenbroeck",
        LocalDate.of(2003, 12, 3),
        "maya.vandenbroeck@gmail.com"
    )
    private val userLuc = User(
        UUID.randomUUID(),
        "Luc Vanhove",
        LocalDate.of(2002, 1, 21),
        "luc.vanhove@gmail.com"
    )
    private val userAnne = User(
        UUID.randomUUID(),
        "Anne Verheggen",
        LocalDate.of(2004, 5, 10),
        "anne.verheggen@gmail.com"
    )
    private val userDavid = User(
        UUID.randomUUID(),
        "David Verhoeven",
        LocalDate.of(2003, 9, 7),
        "david.verhoeven@gmail.com"
    )
    private val userFlorence = User(
        UUID.randomUUID(),
        "Florence Veron",
        LocalDate.of(2005, 7, 23),
        "florence.veron@gmail.com"
    )
    private val userNico = User(
        UUID.randomUUID(),
        "Nico Verpieren",
        LocalDate.of(2002, 11, 2),
        "nico.verpieren@gmail.com"
    )
    private val userGrace = User(
        UUID.randomUUID(),
        "Grace Veuger",
        LocalDate.of(2004, 10, 15),
        "grace.veuger@gmail.com"
    )
    private val userChris = User(
        UUID.randomUUID(),
        "Chris Vierde",
        LocalDate.of(2003, 4, 28),
        "chris.vierde@gmail.com"
    )
    private val userMaria = User(
        UUID.randomUUID(),
        "Maria Villard",
        LocalDate.of(2005, 6, 12),
        "maria.villard@gmail.com"
    )
    private val userSteve = User(
        UUID.randomUUID(),
        "Steve Vivier",
        LocalDate.of(2001, 8, 19),
        "steve.vivier@gmail.com"
    )
    private val userJessica = User(
        UUID.randomUUID(),
        "Jessica Voegele",
        LocalDate.of(2004, 1, 6),
        "jessica.voegele@gmail.com"
    )
    private val userPaul = User(
        UUID.randomUUID(),
        "Paul Vogel",
        LocalDate.of(2003, 3, 17),
        "paul.vogel@gmail.com"
    )
    private val userRosa = User(
        UUID.randomUUID(),
        "Rosa Voigt",
        LocalDate.of(2005, 11, 24),
        "rosa.voigt@gmail.com"
    )

    private val allUsers = listOf(
        userJan, userKoen, userLotte, userMilan, userNora, userBram, userElise,
        userTibo, userMila, userRuben, userNoor, userSanne, userDaan, userLuna,
        userMaxim, userAva, userFelix, userZoe, userJoren, userIsabella,
        userSebastian, userNatasja, userThijs, userStéphanie, userMarkus, userCamille,
        userVictoria, userDieter, userLea, userRyan, userSophie, userQuentin, userEva,
        userLars, userAnna, userTom, userEmilie, userPhilip, userClaire, userSven,
        userBeat, userLena, userJulian, userSienna, userAlex, userOliver, userMaya,
        userLuc, userAnne, userDavid, userFlorence, userNico, userGrace, userChris,
        userMaria, userSteve, userJessica, userPaul, userRosa
    )

    fun getHangouts(): List<HangoutDto> {
        return listOf(
            HangoutDto(
                UUID.randomUUID(),
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
                UUID.randomUUID(),
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
                UUID.randomUUID(),
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
                UUID.randomUUID(),
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
                UUID.randomUUID(),
                "No other choice - Movie Night",
                "",
                "Cinema Lumiere Brugge",
                51.2114,
                3.2272,
                LocalDateTime.of(2026, 4, 19, 19, 30),
                listOf(userMila, userRuben, userLotte, userNoor),
                userMila,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Fuse Crew Night",
                "",
                "Fuse Bruxelles",
                50.8500,
                4.3632,
                LocalDateTime.of(2026, 4, 18, 23, 0),
                listOf(userSanne, userDaan, userThijs, userRyan),
                userDaan,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Modul'air Open Air",
                "",
                "Modul'air Arlon",
                49.6833,
                5.8000,
                LocalDateTime.of(2026, 4, 20, 22, 0),
                listOf(userLuna, userMaxim, userAva, userFelix, userZoe),
                userLuna,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Huisfeestje Leuven",
                "",
                "Studentenkamer Leuven",
                50.8798,
                4.7005,
                LocalDateTime.of(2026, 4, 17, 20, 0),
                listOf(userJoren, userIsabella, userSebastian, userNatasja),
                userJoren,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Café Uitstap Antwerpen",
                "",
                "Café Puur Antwerpen",
                51.2194,
                4.4024,
                LocalDateTime.of(2026, 4, 16, 19, 0),
                listOf(userThijs, userStéphanie, userMarkus),
                userThijs,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Kompass Rave Squad",
                "",
                "Kompass Klub Gent",
                51.0378,
                3.7042,
                LocalDateTime.of(2026, 4, 25, 23, 30),
                listOf(userCamille, userVictoria, userDieter, userLea, userRyan),
                userCamille,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Thuisfeestje Brussel",
                "",
                "Loft Sint-Gillis",
                50.8548,
                4.3457,
                LocalDateTime.of(2026, 4, 19, 21, 0),
                listOf(userSophie, userQuentin, userEva, userLars),
                userSophie,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Kroegentour Gent",
                "",
                "Brouwerij Oud Beersel",
                51.0356,
                3.7161,
                LocalDateTime.of(2026, 4, 21, 20, 0),
                listOf(userAnna, userTom, userEmilie),
                userTom,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Festival Circuit - Vrienden",
                "",
                "Sportpaleis Antwerpen",
                51.2183,
                4.4141,
                LocalDateTime.of(2026, 5, 1, 14, 0),
                listOf(userPhilip, userClaire, userSven, userBeat, userLena),
                userPhilip,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Fuse Rave Crew",
                "",
                "Fuse Bruxelles",
                50.8500,
                4.3632,
                LocalDateTime.of(2026, 4, 23, 23, 0),
                listOf(userJulian, userSienna, userAlex),
                userJulian,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Student Huisfeest",
                "",
                "KU Leuven Campus",
                50.8798,
                4.7005,
                LocalDateTime.of(2026, 4, 22, 19, 0),
                listOf(userOliver, userMaya, userLuc, userAnne),
                userOliver,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Café Night Out Brussel",
                "",
                "Bar Bram Bruxelles",
                50.8487,
                4.3572,
                LocalDateTime.of(2026, 4, 20, 19, 30),
                listOf(userDavid, userFlorence),
                userDavid,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Modul'air Festival Crew",
                "",
                "Modul'air Arlon",
                49.6833,
                5.8000,
                LocalDateTime.of(2026, 4, 26, 22, 0),
                listOf(userNico, userGrace, userChris, userMaria),
                userNico,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Predrinks Antwerpen",
                "",
                "Studentenhuis Antwerpen",
                51.2194,
                4.4024,
                LocalDateTime.of(2026, 4, 18, 21, 0),
                listOf(userSteve, userJessica, userPaul, userRosa),
                userSteve,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Kompass Grand Opening",
                "",
                "Kompass Klub Gent",
                51.0378,
                3.7042,
                LocalDateTime.of(2026, 4, 27, 23, 0),
                listOf(userSanne, userDaan, userLuna, userMaxim, userAva),
                userDaan,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Whiskey Tasting Gent",
                "",
                "Whiskey Café Gent",
                51.0519,
                3.7176,
                LocalDateTime.of(2026, 4, 19, 19, 0),
                listOf(userFelix, userZoe, userJoren),
                userFelix,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Tuinfeestje VUB",
                "",
                "VUB Campus Brussel",
                50.8224,
                4.3948,
                LocalDateTime.of(2026, 4, 24, 18, 0),
                listOf(userIsabella, userSebastian, userNatasja, userThijs),
                userIsabella,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Pint & Pizza Night",
                "",
                "Pizzeria Napoli Leuven",
                50.8798,
                4.7005,
                LocalDateTime.of(2026, 4, 21, 19, 30),
                listOf(userStéphanie, userMarkus, userCamille, userVictoria),
                userMarkus,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Rave Voorbereiding",
                "",
                "Flat Brussel",
                50.8500,
                4.3600,
                LocalDateTime.of(2026, 4, 23, 20, 0),
                listOf(userDieter, userLea, userRyan, userSophie),
                userRyan,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Beach Party Knokke",
                "",
                "Strand Knokke",
                51.3557,
                3.2796,
                LocalDateTime.of(2026, 5, 2, 15, 0),
                listOf(userQuentin, userEva, userLars, userAnna, userTom),
                userQuentin,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "House Music Night Gent",
                "",
                "Muziekclub Gent",
                51.0432,
                3.7299,
                LocalDateTime.of(2026, 4, 25, 23, 0),
                listOf(userEmilie, userPhilip, userClaire),
                userEmilie,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Studentenkamer Feestje",
                "",
                "Thuis Leuven",
                50.8798,
                4.7005,
                LocalDateTime.of(2026, 4, 28, 20, 0),
                listOf(userSven, userBeat, userLena),
                userBeat,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Craft Beer Tour",
                "",
                "Brouwerijen Route Brugge",
                51.2105,
                3.2223,
                LocalDateTime.of(2026, 4, 22, 18, 0),
                listOf(userJulian, userSienna, userAlex, userOliver),
                userJulian,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Spring Festival Squad",
                "",
                "Citadelpark Antwerpen",
                51.2135,
                4.4005,
                LocalDateTime.of(2026, 5, 3, 14, 0),
                listOf(userMaya, userLuc, userAnne, userDavid),
                userMaya,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Club Night Brussel",
                "",
                "Fuse Bruxelles",
                50.8500,
                4.3632,
                LocalDateTime.of(2026, 4, 24, 23, 30),
                listOf(userFlorence, userNico, userGrace),
                userFlorence,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Vibes at Home",
                "",
                "Huisje Gent",
                51.0432,
                3.7299,
                LocalDateTime.of(2026, 4, 29, 21, 0),
                listOf(userChris, userMaria, userSteve, userJessica),
                userChris,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Modul'air Weekend Crew",
                "",
                "Modul'air Arlon",
                49.6833,
                5.8000,
                LocalDateTime.of(2026, 4, 28, 22, 0),
                listOf(userPaul, userRosa, userSanne),
                userPaul,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Spontane Campus Hang",
                "",
                "Terras UGent Gent",
                51.0088,
                3.7153,
                LocalDateTime.of(2026, 4, 20, 16, 0),
                listOf(userDaan, userLuna, userMaxim, userAva, userFelix, userZoe),
                userZoe,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Techno Session Gent",
                "",
                "Kompass Klub Gent",
                51.0378,
                3.7042,
                LocalDateTime.of(2026, 4, 30, 23, 0),
                listOf(userJoren, userIsabella, userSebastian),
                userJoren,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Beachclub Zomer",
                "",
                "Beachclub Ostende",
                51.2247,
                2.9267,
                LocalDateTime.of(2026, 5, 4, 16, 0),
                listOf(userNatasja, userThijs, userStéphanie, userMarkus, userCamille),
                userNatasja,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Afterparty Bruxelles",
                "",
                "Loft Brussel",
                50.8548,
                4.3457,
                LocalDateTime.of(2026, 4, 25, 3, 0),
                listOf(userVictoria, userDieter, userLea),
                userVictoria,
                true
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Summer Kick-off Party",
                "",
                "Tuin VUB",
                50.8224,
                4.3948,
                LocalDateTime.of(2026, 5, 10, 14, 0),
                listOf(userRyan, userSophie, userQuentin, userEva, userLars),
                userEva,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Wine & Dine Antwerpen",
                "",
                "Restaurant Bellini Antwerpen",
                51.2194,
                4.4024,
                LocalDateTime.of(2026, 4, 26, 19, 0),
                listOf(userAnna, userTom, userEmilie, userPhilip),
                userAnna,
                false
            ),
            HangoutDto(
                UUID.randomUUID(),
                "Rave Afterparty",
                "",
                "Woning Leuven",
                50.8798,
                4.7005,
                LocalDateTime.of(2026, 4, 27, 5, 0),
                listOf(userClaire, userSven, userBeat),
                userClaire,
                true
            )
        )
    }

    fun getHangoutById(id: UUID): HangoutDto {
        return getHangouts().first { id == it.id }
    }
}