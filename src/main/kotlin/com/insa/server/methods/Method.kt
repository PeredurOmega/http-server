package com.insa.server.methods

import com.insa.server.Client
import com.insa.server.ResponseStatus
import com.insa.server.exceptions.NoPathProvided
import com.insa.server.exceptions.UnknownMethodException
import java.io.*
import java.net.URLDecoder
import java.util.*

abstract class Method(var client: Client) {
    private var queryString: HashMap<String?, String?>? = null

    @Throws(IOException::class)
    abstract fun execute()

    protected fun printQueryString() {
        printParameters(queryString, "QueryString data :")
    }

    protected fun printParameters(parameters: HashMap<String?, String?>?, title: String) {
        if (parameters!!.size > 0) {
            client.appendResponseLine("<h1>$title</h1>")
            client.appendResponseLine("<ul>")

            println("[Method] DATA PARAMS:")
            val it: Iterator<*> = parameters.entries.iterator()
            while (it.hasNext()) {
                val (key, value) = it.next() as Map.Entry<*, *>
                println(key.toString() + ": " + value)
                client.appendResponseLine("<li><b>$key</b>: $value</li>")
            }
            client.appendResponseLine("</ul>")
            println("----------------------------")
        } else println("[Method] No query string.")
    }

    @Throws(IOException::class)
    protected fun sendFile(): Boolean {
        var path = client.retrieveRequestHeader()["path"] ?: throw NoPathProvided()
        if (path == "/") {
            path = "/html/index.html"
        }
        return try {
            val fileReader = readFile(path)

            client.setResponseHeader(ResponseStatus.OK, "Content-Type: text/html\r\n")
            while (fileReader.ready()) {
                client.appendResponseLine(fileReader.readLine())
            }
            true
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            client.setResponseHeader(ResponseStatus.NOT_FOUND, null)
            false
        }
    }

    @Throws(FileNotFoundException::class)
    protected fun readFile(webPath: String): BufferedReader {
        val fileReader = FileReader(getSysPath(webPath))
        val reader = BufferedReader(fileReader)
        println("[Method] File $webPath loaded.")
        return reader
    }

    init {
        queryString = try {
            val requestHeader = client.retrieveRequestHeader()
            val query = (if (requestHeader.containsKey("querystring")) requestHeader["querystring"] else "") ?: ""
            decodeQueryString(query)
        } catch (e: IOException) {
            e.printStackTrace()
            HashMap()
        }
    }

    companion object {
        @JvmStatic
        fun getSysPath(webPath: String) = "src/main/resources" + if (webPath.startsWith("/")) webPath else webPath

        @JvmStatic
        fun decodeQueryString(query: String): HashMap<String?, String?> {
            val parameters = HashMap<String?, String?>()
            if (query.isNotEmpty()) {
                val rawParams = query.split("&".toRegex()).toTypedArray()
                var paramData: Array<String?>
                if (rawParams.isNotEmpty()) {
                    for (rawParam in rawParams) {
                        paramData = rawParam.split("=".toRegex()).toTypedArray()
                        try {
                            parameters[URLDecoder.decode(paramData[0], "UTF-8")] =
                                URLDecoder.decode(paramData[1], "UTF-8")
                        } catch (e: UnsupportedEncodingException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            return parameters
        }

        @Throws(UnknownMethodException::class)
        fun factory(type: String?, client: Client): Method {
            return when (type?.lowercase(Locale.getDefault())) {
                "get" -> Get(client)
                "post" -> Post(client)
                "put" -> Put(client)
                "delete" -> Delete(client)
                else -> throw UnknownMethodException(type ?: "null")
            }
        }
    }
}