package com.insa.server.methods

import com.insa.server.Client
import com.insa.server.ResponseStatus
import com.insa.server.exceptions.NoPathProvided
import java.io.IOException

class Head(c: Client) : Method(c) {
    @Throws(IOException::class)
    override fun execute() {
        val extension =
            client.retrieveRequestHeader()["path"]?.split(Regex("[/|.]"))?.last()?.lowercase() ?: throw NoPathProvided()
        when (extension) {
            "404" -> client.setResponseHeader(ResponseStatus.NOT_FOUND, null)
            "png" -> client.setResponseHeader(ResponseStatus.OK, "Content-Type: image/png;")
            "svg" -> client.setResponseHeader(ResponseStatus.OK, "Content-Type: image/svg;")
            "pdf" -> client.setResponseHeader(ResponseStatus.OK, "Content-Type: application/pdf;")
            "mp3" -> client.setResponseHeader(ResponseStatus.OK, "Content-Type: audio/mpeg;")
            "mp4" -> client.setResponseHeader(ResponseStatus.OK, "Content-Type: video/webm;")
            else -> client.setResponseHeader(ResponseStatus.OK, "Content-Type: text/html; charset=utf-8")
        }
    }
}