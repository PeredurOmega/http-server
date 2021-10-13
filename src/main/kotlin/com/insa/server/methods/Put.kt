package com.insa.server.methods

import com.insa.server.Client
import com.insa.server.ResponseStatus
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter

class Put(c: Client?) : Method(c!!) {
    @Throws(IOException::class)
    override fun execute() {
        val data = client.requestBody
        val path = client.retrieveRequestHeader()["path"] ?: ""
        try {
            val fileIsNew = writeFile(path, data)
            client.setResponseHeader(if (fileIsNew) ResponseStatus.CREATED else ResponseStatus.OK, null)
        } catch (e: Exception) {
            e.printStackTrace()
            client.setResponseHeader(ResponseStatus.INTERNAL_ERROR, null)
        }
    }

    @Throws(FileCreationException::class, IOException::class)
    private fun writeFile(path: String, data: String?): Boolean {
        val file = File(getSysPath(path))
        var newFile = true
        if (file.exists()) {
            file.delete()
            newFile = false
        }
        val fileCreated = file.createNewFile()
        if (fileCreated) {
            val writer = PrintWriter(FileWriter(file))
            writer.println(data)
            writer.flush()
            writer.close()
        } else {
            throw FileCreationException()
        }
        return newFile
    }

    private inner class FileCreationException : Exception()
}