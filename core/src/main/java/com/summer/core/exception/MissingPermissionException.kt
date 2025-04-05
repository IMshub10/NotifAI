package com.summer.core.exception

class MissingPermissionException(private val exceptionMessage: String) : IllegalStateException() {
    override val message: String
        get() = "Missing required permissions. Please grant the necessary permissions to proceed. $exceptionMessage"
}