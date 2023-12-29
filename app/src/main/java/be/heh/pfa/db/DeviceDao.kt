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

    @Update
    fun updateDevice(device: DeviceRecord)

    @Query("UPDATE devices SET isBorrowed = :isBorrowed WHERE id = :deviceId")
    fun updateIsBorrowedStatus(deviceId: Long, isBorrowed: Boolean)

    @Delete
    fun deleteDevice(device: DeviceRecord)
}