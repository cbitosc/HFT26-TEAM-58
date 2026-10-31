package com.team58.emergencymessaging.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EmergencyMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: EmergencyMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<EmergencyMessage>)

    @Query("SELECT * FROM emergency_messages ORDER BY timestamp DESC")
    suspend fun getAllMessages(): List<EmergencyMessage>

    @Query("SELECT * FROM emergency_messages WHERE messageId = :id LIMIT 1")
    suspend fun getMessageById(id: String): EmergencyMessage?

    @Query("SELECT * FROM emergency_messages WHERE status = :messageStatus ORDER BY timestamp DESC")
    suspend fun getMessagesByStatus(messageStatus: String): List<EmergencyMessage>

    @Query("DELETE FROM emergency_messages WHERE expiryTime <= :currentTime")
    suspend fun deleteExpiredMessages(currentTime: Long): Int

    @Delete
    suspend fun deleteMessage(message: EmergencyMessage)
}