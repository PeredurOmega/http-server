package com.insa.server.methods

import com.insa.server.Client
import kotlin.Throws
import java.io.IOException

class Get(c: Client) : Method(c) {
    @Throws(IOException::class)
    override fun execute() {
        sendFile()
        printQueryString()
    }
}