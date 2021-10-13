package com.insa.server.methods

import com.insa.server.Client
import com.insa.server.ResponseStatus
import java.util.HashMap
import kotlin.Throws
import java.io.IOException

class Post(c: Client) : Method(c) {
    private var parameters: HashMap<String?, String?>? = null

    @Throws(IOException::class)
    override fun execute() {
        if (retrieveParams()) {
            if (sendFile()) {
                printParameters(parameters, "POST - Data from the form:")
                printQueryString()
            } else {
                println("[Post] Not found file.")
            }
        } else {
            client.setResponseHeader(ResponseStatus.NO_CONTENT, null)
        }
    }

    private fun retrieveParams(): Boolean {
        try {
            val requestBody = client.requestBody
            parameters = decodeQueryString(requestBody!!)
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }
}