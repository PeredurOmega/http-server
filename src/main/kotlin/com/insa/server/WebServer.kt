package com.insa.server

import java.net.ServerSocket

class WebServer {
    private fun start() {
        // Create the main server socket
        val s = ServerSocket(3000)
        println("Waiting for connection")
        while (true) {
            try {
                // Waiting for a connection
                val remote = s.accept()
                // Remote is now the connected socket
                println("Connection, sending data.")
                val client = Client(remote)
                RequestHandler(client).start()
            } catch (e: Exception) {
                println("Error: $e")
            }
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val ws = WebServer()
            ws.start()
        }
    }
}