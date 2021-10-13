package com.insa.server

/**
 * Status of the response.
 * See more at https://developer.mozilla.org/fr/docs/Web/HTTP/Status.
 */
enum class ResponseStatus(var code: Int) {
    OK(200),
    CREATED(201),
    NOT_FOUND(404),
    FORBIDDEN(403),
    INTERNAL_ERROR(500),
    GATEWAY_TIMEOUT(504),
    NO_CONTENT(204),
    BAD_REQUEST(400);

    override fun toString(): String {
        val name = name
        println("[ResponseStatus] Sending $name")
        return "HTTP/1.1 $code $name"
    }
}