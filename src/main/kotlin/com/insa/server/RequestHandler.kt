package com.insa.server

import com.insa.server.exceptions.UnknownMethodException
import com.insa.server.methods.Method
import java.lang.Thread
import java.io.IOException

class RequestHandler(private val client: Client) : Thread() {
    /**
     * Executing the method requested by the client.
     */
    override fun run() {
        try {
            val header = client.retrieveRequestHeader()
            try {
                val method = Method.factory(header["method"], client)
                method.execute()
            } catch (e: UnknownMethodException) {
                e.printStackTrace()
                client.appendResponseLine(ResponseStatus.BAD_REQUEST.toString())
            }
            end()
        } catch (e: IOException) {
            e.printStackTrace()
            client.appendResponseLine(ResponseStatus.INTERNAL_ERROR.toString())
            end()
        }
    }

    private fun end() {
        client.endConnection()
        interrupt()
    }
}