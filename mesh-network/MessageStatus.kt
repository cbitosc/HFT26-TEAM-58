```kotlin
/**
 * Tracks the delivery state of an emergency message.
 */
enum class MessageState {
    PENDING,
    SENT,
    RELAYED,
    DELIVERED,
    FAILED
}

/**
 * Stores the latest known state for each message.
 */
class MessageStatusTracker {

    private val statuses = mutableMapOf<String, MessageState>()

    fun updateStatus(messageId: String, state: MessageState) {
        statuses[messageId] = state
    }

    fun getStatus(messageId: String): MessageState {
        return statuses[messageId] ?: MessageState.PENDING
    }

    fun getAllStatuses(): Map<String, MessageState> {
        return statuses.toMap()
    }
}
```