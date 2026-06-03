package be.runeherreman.zuyp.ui.hangout.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID

fun createExpenseImageFile(context: Context): File =
    File(context.filesDir, "expense_images").apply { mkdirs() }
        .let { File(it, "${UUID.randomUUID()}.jpg") }

fun fileUri(context: Context, file: File): Uri =
    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

// gallery pick → copy into filesDir, return path
fun copyIntoAppStorage(context: Context, source: Uri): String {
    val dest = createExpenseImageFile(context)
    context.contentResolver.openInputStream(source)!!.use { input ->
        dest.outputStream().use { input.copyTo(it) }
    }
    return dest.absolutePath
}