package com.example.offlineemergencymessaging.network

/**
 * Represents an emergency message that can be shared between nearby devices.
 */
data class MeshMessage(
    val messageId: String,
    val senderId: String,
    val recipientId: String? = null,
    val messageText: String,
    val priority: MessagePriority = MessagePriority.NORMAL,
    val timestamp: Long = System.currentTimeMillis(),
    val hopCount: Int = 0
)

enum class MessagePriority {
    NORMAL,
    HIGH,
    SOS
}