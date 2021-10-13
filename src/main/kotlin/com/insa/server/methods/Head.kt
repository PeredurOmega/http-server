package com.insa.server.methods

import com.insa.server.Client
import java.io.IOException

class Head(c: Client) : Method(c) {
    @Throws(IOException::class)
    override fun execute() {
        setResponseHeaderFromRequestedFile()
    }
}