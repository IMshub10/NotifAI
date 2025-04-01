package com.summer.core.exception

class MissingPermissionException : IllegalStateException() {
    override val message: String
        get() = "Missing required permissions. Please grant the necessary permissions to proceed."
}