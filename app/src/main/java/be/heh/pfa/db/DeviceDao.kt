package be.heh.pfa.db

import androidx.room.Dao
import androidx.room.*

@Dao
interface DeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDevice(device: DeviceRecord)

    @Query("SELECT * FROM devices")
    fun getAllDevices(): List<DeviceRecord>

    @Query("SELECT * FROM devices WHERE id = :deviceId")
    fun getDeviceById(deviceId: Long): DeviceRecord?

    @Query("SELECT * FROM devices WHERE referenceNumber = :deviceReferenceNumber")
    fun getDeviceByReferenceNumber(deviceReferenceNumber: String): DeviceRecord?

    @Query("Select Exists(SELECT * FROM devices WHERE referenceNumber = :deviceReferenceNumber)")
    fun isReferenceNumberAlreadyUsed(deviceReferenceNumber: String): Boolean


    @Update
    fun updateDevice(device: DeviceRecord)

    @Query("UPDATE devices SET isBorrowed = :isBorrowed WHERE id = :deviceId")
    fun updateIsBorrowedStatus(deviceId: Long, isBorrowed: Boolean)

    @Delete
    fun deleteDevice(device: DeviceRecord)
}