```kotlin
/**
 * Routes emergency messages to connected devices.
 *
 * This component prepares messages for forwarding.
 * Actual transmission will be implemented using
 * Google Nearby Connections.
 */
class MessageRelay {

    private val processedMessageIds = mutableSetOf<String>()

    /**
     * Returns true when this message has not been processed before.
     */
    fun shouldProcess(message: EmergencyMessage): Boolean {
        return processedMessageIds.add(message.id)
    }

    /**
     * Selects messages that should be forwarded.
     *
     * A message is forwarded only once by this relay component.
     * The actual network send operation will be added later.
     */
    fun prepareForRelay(
        message: EmergencyMessage,
        connectedDevices: List<MeshDevice>,
        currentDeviceId: String
    ): List<MeshDevice> {

        if (!shouldProcess(message)) {
            return emptyList()
        }

        return connectedDevices.filter { device ->
            device.isConnected &&
                device.endpointId != currentDeviceId &&
                device.endpointId != message.senderId &&
                device.endpointId != message.recipientId
        }
    }
}
```