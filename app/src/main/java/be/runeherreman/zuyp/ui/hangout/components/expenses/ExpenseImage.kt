package be.runeherreman.zuyp.ui.hangout.components.expenses

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID

/** filesDir/expense_images, created on demand. Matches res/xml/file_paths.xml. */
private fun expenseImagesDir(context: Context): File =
    File(context.filesDir, "expense_images").apply { mkdirs() }

/** A fresh empty file the camera can write into. */
fun newExpenseImageFile(context: Context): File =
    File(expenseImagesDir(context), "${UUID.randomUUID()}.jpg")

/** Content uri for [file] via the app FileProvider (needed by the camera intent). */
fun expenseImageUri(context: Context, file: File): Uri =
    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

/** Copies a picked gallery image into app storage and returns the saved path. */
fun copyImageIntoAppStorage(context: Context, source: Uri): String {
    val dest = newExpenseImageFile(context)
    context.contentResolver.openInputStream(source)?.use { input ->
        dest.outputStream().use { output -> input.copyTo(output) }
    }
    return dest.absolutePath
}
