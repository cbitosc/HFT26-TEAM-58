package com.emergency.app

data class MessageRequest(
    val message: String
)

data class TranslationRequest(
    val message: String,
    val target_language: String
)

data class SummaryResponse(
    val success: Boolean,
    val response: String
)

data class ClassificationResponse(
    val success: Boolean,
    val classification: String,
    val reason: String? = null
)

data class TranslationResponse(
    val success: Boolean,
    val translated_message: String
)