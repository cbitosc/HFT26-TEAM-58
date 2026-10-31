package com.example.offlineemergencymessaging.network

/**
 * Decides whether a message should be relayed to another device.
 *
 * This is routing logic only; it does not send messages over Bluetooth or Wi-Fi.
 */
class MeshRouter(
    private val maxHops: Int = 5
) {
    private val seenMessageIds = mutableSetOf<String>()

    /**
     * Returns true if this message has not been seen before
     * and is still eligible for forwarding.
     */
    @Synchronized
    fun shouldRelay(message: MeshMessage): Boolean {
        if (message.messageId.isBlank()) return false
        if (message.messageId in seenMessageIds) return false
        if (message.hopCount >= maxHops) return false

        seenMessageIds.add(message.messageId)
        return true
    }

    /**
     * Creates a copy for forwarding to another device.
     */
    fun prepareRelay(message: MeshMessage): MeshMessage {
        return message.copy(
            hopCount = message.hopCount + 1
        )
    }
}