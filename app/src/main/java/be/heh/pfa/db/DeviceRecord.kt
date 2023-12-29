package be.heh.pfa.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class DeviceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String,
    val brand: String,
    val model: String,
    val referenceNumber: String,
    val manufacturerWebsite: String,
    val qrCode: String,
    val isBorrowed: Boolean
)