package com.example.offlineemergencymessaging.network

/**
 * Temporary in-memory message store.
 * A future version should use Room so messages survive app restarts.
 */
class MeshMessageStore {

    private val messages = linkedMapOf<String, MeshMessage>()

    @Synchronized
    fun save(message: MeshMessage) {
        messages.putIfAbsent(message.messageId, message)
    }

    @Synchronized
    fun get(messageId: String): MeshMessage? {
        return messages[messageId]
    }

    @Synchronized
    fun getAll(): List<MeshMessage> {
        return messages.values.toList()
    }

    @Synchronized
    fun contains(messageId: String): Boolean {
        return messages.containsKey(messageId)
    }
}