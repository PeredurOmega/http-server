package com.insa.server.methods

import com.insa.server.Client
import com.insa.server.ResponseStatus
import java.io.IOException

class Post(c: Client) : Method(c) {
    private var parameters: HashMap<String?, String?>? = null

    @Throws(IOException::class)
    override fun execute() {
        if (retrieveParams()) {
            setResponseHeaderFromRequestedFile()
            printParameters(parameters, "POST - Data from the form:")
            printQueryString()
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