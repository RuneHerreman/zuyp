package be.runeherreman.zuyp.ui.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import be.runeherreman.zuyp.domain.model.Hangout

fun openMapsForHangout(hangout: Hangout, context: Context) {
    val uri =
        "geo:${hangout.latitude},${hangout.longitude}?q=${hangout.latitude},${hangout.longitude}(${hangout.title})"
            .toUri()

    val mapIntent = Intent(Intent.ACTION_VIEW, uri)
    val chooser = Intent.createChooser(mapIntent, "Navigate to ${hangout.title}")

    context.startActivity(chooser)
}
