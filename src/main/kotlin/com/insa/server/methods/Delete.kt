package com.insa.server.methods

import com.insa.server.Client
import com.insa.server.ResponseStatus
import com.insa.server.exceptions.AbsentFileException
import com.insa.server.exceptions.NoPathProvided
import com.insa.server.exceptions.PermissionException
import java.io.File
import java.io.IOException

/**
 * Delete a file if it exists.
 */
class Delete(c: Client) : Method(c) {
    @Throws(IOException::class)
    override fun execute() {
        val path = client.retrieveRequestHeader()["path"] ?: throw NoPathProvided()
        try {
            deleteFile(path)
            client.setResponseHeader(ResponseStatus.OK, null)
        } catch (e: Exception) {
            e.printStackTrace()
            client.setResponseHeader(ResponseStatus.INTERNAL_ERROR, null)
        }
    }

    /**
     * @param path Path of the file to delete.
     * @throws AbsentFileException
     * @throws PermissionException
     */
    @Throws(AbsentFileException::class, PermissionException::class)
    private fun deleteFile(path: String) {
        val file = File(getSysPath(path))
        if (!file.exists()) throw AbsentFileException()
        if (!file.delete()) throw PermissionException()
    }
}