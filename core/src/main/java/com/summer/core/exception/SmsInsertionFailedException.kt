package com.summer.core.exception

class SmsInsertionFailedException(private val exceptionMessage: String) : IllegalStateException() {
    override val message: String
        get() = "Sms Insertion Failed! Please check if there is any issue with the SMS provider or retrieving sms. $exceptionMessage"
}