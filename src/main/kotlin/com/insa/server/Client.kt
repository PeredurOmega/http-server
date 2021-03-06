package com.insa.server

import java.io.*
import java.net.Socket
import java.nio.file.Files
import java.util.*

/**
 * Client class containing socket and requests with body and header.
 */
class Client(s: Socket) {
    private val socket: Socket
    private val reader: BufferedReader
    private val out: PrintWriter
    private val outputStream: OutputStream

    /**
     * method - Type of the requested method.
     * path - Path to a specific resource.
     * queryString - Peculiar cases.
     */
    private var requestHeader: HashMap<String, String>? = null

    @get:Throws(IOException::class)
    var requestBody: String? = null
        get() {
            if (field == null) {
                val requestBodyBuilder = StringBuilder()
                val header = retrieveRequestHeader()
                if (header.containsKey("content-length")) {
                    val contentLength = header["content-length"]!!.toInt()
                    var c: Int
                    for (i in 0 until contentLength) {
                        c = reader.read()
                        if (c >= 0) requestBodyBuilder.append(c.toChar())
                    }
                }
                field = requestBodyBuilder.toString()
            }
            return field
        }
        private set
    private var setResponseHeader = false

    /**
     * Build the requestHeader from the input stream.
     *
     * @return Returns a hash map of the request header.
     * @throws IOException May throw an input output exception.
     */
    @Throws(IOException::class)
    fun retrieveRequestHeader(): HashMap<String, String> {
        if (requestHeader == null) {
            requestHeader = HashMap()
            var str = reader.readLine()
            while (str != null && str != "") { // While we are not on an empty line
                if (str.contains("HTTP/1.1")) { // If it's the requested method (e.g.: GET / HTTP/1.1)
                    val requestType = str.split("\\s+".toRegex()).toTypedArray() // Splitting whitespaces
                    if (requestType.size == 3) {
                        requestHeader!!["method"] = requestType[0] // Storing requested method
                        if (requestType[1].contains("?")) { // Querystring case
                            val getPath = requestType[1].split("\\?".toRegex()).toTypedArray()
                            // Storing the querystring
                            requestType[1] = getPath[0]
                            requestHeader!!["querystring"] = getPath[1]
                        }
                        requestHeader!!["path"] = requestType[1]
                    }
                } else if (str.contains(":")) { // Headers basic components
                    val request = str.split(":".toRegex()).toTypedArray()
                    if (request.size == 2) {
                        requestHeader!![request[0].trim { it <= ' ' }.lowercase(Locale.getDefault())] =
                            request[1].trim { it <= ' ' }
                    }
                }
                str = reader.readLine()
            }
        }
        return requestHeader!!
    }

    fun setResponseHeader(response: ResponseStatus, additionalDetails: String?) {
        if (!setResponseHeader) {
            out.println(response.toString())
            if (additionalDetails != null) out.println(additionalDetails)
            out.println()
            setResponseHeader = true
        }
    }

    fun appendResponseLine(str: String?) {
        out.println(str)
    }

    fun writeFile(file: File) {
        //Files.copy(inputStream.toPath(), socket.getOutputStream())
        var count: Int
        outputStream.write("HTTP/1.1 200 OK\n".toByteArray())
        outputStream.write("Content-Type: ${Files.probeContentType(file.toPath())};\n".toByteArray())
        outputStream.write("\n".toByteArray())
        //Files.copy(inputStream.toPath(), outputStream)
        val buffer = ByteArray(16000) // or 4096, or more
        val isp = file.inputStream()
        while (isp.read(buffer).also { count = it } > 0) {
            outputStream.write(buffer, 0, count)
            //out.write(buffer.toString(Charset.defaultCharset()), 0, count)
        }

        outputStream.flush()
        outputStream.close()

    }

    /**
     * Close connection with the user.
     */
    fun endConnection() {
        println("[Client] Ending connection for user " + socket.inetAddress.hostAddress + ":" + socket.port)
        out.println()
        out.flush()
        out.close()
        try {
            reader.close()
            socket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    init {
        println("[Client] Handling new user - " + s.inetAddress.hostAddress + ":" + s.port)
        socket = s
        reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        outputStream = socket.getOutputStream()
        out = PrintWriter(outputStream)
    }
}