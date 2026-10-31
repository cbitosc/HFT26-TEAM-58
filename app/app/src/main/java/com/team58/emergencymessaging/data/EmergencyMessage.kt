package com.team58.emergencymessaging.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_messages")
data class EmergencyMessage(
    @PrimaryKey
    val messageId: String,

    val senderId: String,
    val receiverId: String?,
    val messageType: String,
    val priority: Int,
    val encryptedPayload: String,
    val timestamp: Long,
    val hopCount: Int,
    val maxHops: Int,
    val expiryTime: Long,
    val status: String
)