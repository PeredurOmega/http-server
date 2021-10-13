package com.insa.server.exceptions

class UnknownMethodException(method: String) : Exception("Unknown method with '$method'")