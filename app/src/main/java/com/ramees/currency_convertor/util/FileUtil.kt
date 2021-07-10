package com.ramees.currency_convertor.util

import android.content.Context
import java.io.OutputStreamWriter


class FileUtil(private val context: Context) {

    fun replaceInInternalFile(txt: String, fileName: String) {
        OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE)).use {
            it.write(txt)
            it.close()
        }
    }

    fun readFromInternalFile(fileName: String): String {
        if (!context.getFileStreamPath(fileName).exists()) return ""

        return context.openFileInput(fileName)?.use { fio ->
            fio.readBytes().toString(Charsets.UTF_8)
        } ?: ""
    }

}