/**
 * Offline Emergency Messaging System
 * Mesh Networking Module
 *
 * Responsibilities:
 * - Discover nearby devices
 * - Manage device connections
 * - Send and receive emergency messages
 * - Relay messages between devices
 */

data class EmergencyMessage(
    val id: String,
    val senderId: String,
    val recipientId: String?,
    val message: String,
    val priority: String = "NORMAL"
)

class MeshNetworking {

    fun createMessage(
        senderId: String,
        recipientId: String?,
        message: String,
        priority: String = "NORMAL"
    ): EmergencyMessage {
        return EmergencyMessage(
            id = java.util.UUID.randomUUID().toString(),
            senderId = senderId,
            recipientId = recipientId,
            message = message,
            priority = priority
        )
    }
}