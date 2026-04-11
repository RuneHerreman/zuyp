package be.runeherreman.zuyp

import android.app.Application
import be.runeherreman.zuyp.data.local.room.database.DatabaseSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltAndroidApp
class ZuypApplication : Application() {
	@Inject
	lateinit var databaseSeeder: DatabaseSeeder

	override fun onCreate() {
		super.onCreate()
		runBlocking {
			databaseSeeder.seedIfNeeded()
		}
	}
}

