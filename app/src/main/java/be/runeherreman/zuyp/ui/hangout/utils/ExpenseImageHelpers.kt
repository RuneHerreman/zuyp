package be.runeherreman.zuyp.ui.hangout.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID

private fun expenseImagesDir(context: Context): File = File(context.filesDir, "expense_images").apply { mkdirs() }
fun newExpenseImageFile(context: Context): File = File(expenseImagesDir(context), "${UUID.randomUUID()}.jpg")
fun expenseImageUri(context: Context, file: File): Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
fun copyImageIntoAppStorage(context: Context, source: Uri): String {
    val dest = newExpenseImageFile(context)
    context.contentResolver.openInputStream(source)?.use { input ->
        dest.outputStream().use { output -> input.copyTo(output) }
    }
    return dest.absolutePath
}
